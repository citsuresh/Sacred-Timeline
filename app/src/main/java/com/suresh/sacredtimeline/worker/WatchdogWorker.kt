package com.suresh.sacredtimeline.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.suresh.sacredtimeline.data.RemindersRepository
import com.suresh.sacredtimeline.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class WatchdogWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val WORK_NAME = "SacredTimelineWatchdogWorker"

        fun enqueuePeriodicWork(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<WatchdogWorker>(
                2, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
            Log.d("WatchdogWorker", "Enqueued periodic WatchdogWorker")
        }

        suspend fun getComponentState(context: Context, componentKey: String): String {
            val settingsRepo = SettingsRepository(context)
            if (settingsRepo.isComponentManuallyStopped(componentKey).first()) {
                return "STOPPED_BY_USER"
            }

            val workManager = WorkManager.getInstance(context)
            val workInfos = withContext(Dispatchers.IO) {
                when (componentKey) {
                    "WidgetUpdateWorker" -> workManager.getWorkInfosForUniqueWork("PanchangamWidgetUpdateWorker").get()
                    "ReminderWorker" -> {
                        val reminders = RemindersRepository(context).getAllReminders().filter { it.isEnabled }
                        if (reminders.isEmpty()) return@withContext emptyList()
                        reminders.mapNotNull { r ->
                            workManager.getWorkInfosForUniqueWork("reminder_${r.id}").get().firstOrNull()
                        }
                    }
                    else -> emptyList()
                }
            }

            if (workInfos.isEmpty()) return "ENQUEUED"
            val info = workInfos.firstOrNull() ?: return "ENQUEUED"
            return when (info.state) {
                WorkInfo.State.RUNNING -> "RUNNING"
                WorkInfo.State.ENQUEUED -> "ENQUEUED"
                WorkInfo.State.SUCCEEDED -> "SUCCEEDED"
                WorkInfo.State.FAILED -> "FAILED"
                WorkInfo.State.CANCELLED -> "CANCELLED"
                else -> "UNKNOWN"
            }
        }

        suspend fun restartComponent(context: Context, componentKey: String) {
            val settingsRepo = SettingsRepository(context)
            settingsRepo.setComponentStopped(componentKey, false)
            settingsRepo.resetFailureCount(componentKey)

            when (componentKey) {
                "WidgetUpdateWorker" -> {
                    WidgetUpdateWorker.enqueuePeriodicWork(context, 15L)
                    WidgetUpdateWorker.triggerImmediateUpdate(context)
                }
                "ReminderWorker" -> {
                    val reminders = RemindersRepository(context).getAllReminders().filter { it.isEnabled }
                    reminders.forEach { r ->
                        ReminderWorker.scheduleReminder(context, r)
                    }
                }
            }
            Log.d("WatchdogWorker", "Manually restarted component: $componentKey")
        }

        suspend fun stopComponent(context: Context, componentKey: String) {
            val settingsRepo = SettingsRepository(context)
            settingsRepo.setComponentStopped(componentKey, true)

            when (componentKey) {
                "WidgetUpdateWorker" -> {
                    WorkManager.getInstance(context).cancelUniqueWork("PanchangamWidgetUpdateWorker")
                }
                "ReminderWorker" -> {
                    val reminders = RemindersRepository(context).getAllReminders()
                    reminders.forEach { r ->
                        ReminderWorker.cancelReminder(context, r.id)
                    }
                }
            }
            Log.d("WatchdogWorker", "Manually stopped component: $componentKey")
        }
    }

    override suspend fun doWork(): Result {
        val settingsRepo = SettingsRepository(context)
        val workManager = WorkManager.getInstance(context)

        // 1. Monitor WidgetUpdateWorker
        val widgetKey = "WidgetUpdateWorker"
        val isWidgetStopped = settingsRepo.isComponentManuallyStopped(widgetKey).first()

        if (!isWidgetStopped) {
            try {
                val workInfos = withContext(Dispatchers.IO) {
                    workManager.getWorkInfosForUniqueWork("PanchangamWidgetUpdateWorker").get()
                }
                val isFailed = workInfos.any { it.state == WorkInfo.State.FAILED || it.state == WorkInfo.State.CANCELLED }

                if (isFailed) {
                    val currentFailures = settingsRepo.incrementFailureCount(widgetKey)
                    if (currentFailures < 3) {
                        Log.w("WatchdogWorker", "WidgetUpdateWorker failed (attempt $currentFailures). Restarting...")
                        WidgetUpdateWorker.enqueuePeriodicWork(context, 15L)
                        WidgetUpdateWorker.triggerImmediateUpdate(context)
                        postWatchdogNotification(
                            "Widget Updater Recovered",
                            "Background widget sync encountered an issue and was successfully restarted."
                        )
                    } else {
                        Log.e("WatchdogWorker", "WidgetUpdateWorker failed $currentFailures times. Circuit breaker tripped!")
                        postWatchdogNotification(
                            "Critical Failure: Widget Updater Paused",
                            "Widget updater failed repeatedly and has been paused to protect battery life. Check Settings."
                        )
                    }
                } else {
                    settingsRepo.resetFailureCount(widgetKey)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Monitor ReminderWorker
        val reminderKey = "ReminderWorker"
        val isReminderStopped = settingsRepo.isComponentManuallyStopped(reminderKey).first()

        if (!isReminderStopped) {
            try {
                val reminders = RemindersRepository(context).getAllReminders().filter { it.isEnabled }
                val failedReminders = withContext(Dispatchers.IO) {
                    reminders.filter { r ->
                        val infos = workManager.getWorkInfosForUniqueWork("reminder_${r.id}").get()
                        infos.any { it.state == WorkInfo.State.FAILED || it.state == WorkInfo.State.CANCELLED }
                    }
                }

                if (failedReminders.isNotEmpty()) {
                    val currentFailures = settingsRepo.incrementFailureCount(reminderKey)
                    if (currentFailures < 3) {
                        Log.w("WatchdogWorker", "Reminders Engine failed (attempt $currentFailures). Rescheduling...")
                        failedReminders.forEach { r -> ReminderWorker.scheduleReminder(context, r) }
                        postWatchdogNotification(
                            "Reminders Engine Recovered",
                            "Some event reminders encountered issues and were successfully rescheduled."
                        )
                    } else {
                        Log.e("WatchdogWorker", "Reminders Engine failed $currentFailures times. Circuit breaker tripped!")
                        postWatchdogNotification(
                            "Critical Failure: Reminders Paused",
                            "Reminders engine failed repeatedly and has been paused. Check Settings."
                        )
                    }
                } else {
                    settingsRepo.resetFailureCount(reminderKey)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return Result.success()
    }

    private fun postWatchdogNotification(title: String, message: String) {
        val channelId = "channel_watchdog"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(title.hashCode(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
