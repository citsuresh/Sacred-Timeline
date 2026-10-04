package com.suresh.sacredtimeline.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.suresh.sacredtimeline.MainActivity
import com.suresh.sacredtimeline.data.RemindersRepository
import com.suresh.sacredtimeline.model.Reminder
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_REMINDER_ID = "key_reminder_id"
        const val KEY_EVENT_TITLE = "key_event_title"
        const val KEY_EVENT_CATEGORY = "key_event_category"
        const val KEY_EVENT_DATE = "key_event_date"
        const val KEY_TIME_DISPLAY = "key_time_display"
        const val KEY_REMINDER_TYPE = "key_reminder_type"

        fun scheduleReminder(context: Context, reminder: Reminder) {
            val triggerMillis = reminder.triggerTime.toEpochMilli()
            val delayMillis = triggerMillis - System.currentTimeMillis()
            if (delayMillis <= 0) {
                return // Do not schedule past reminders
            }

            val inputData = workDataOf(
                KEY_REMINDER_ID to reminder.id,
                KEY_EVENT_TITLE to reminder.eventTitle,
                KEY_EVENT_CATEGORY to reminder.eventCategory,
                KEY_EVENT_DATE to reminder.eventDate.toString(),
                KEY_TIME_DISPLAY to reminder.timeDisplay,
                KEY_REMINDER_TYPE to reminder.reminderType.name
            )

            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "reminder_${reminder.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
            Log.d("ReminderWorker", "Scheduled WorkManager reminder '${reminder.eventTitle}' with delay ${delayMillis}ms")
        }

        fun cancelReminder(context: Context, reminderId: String) {
            WorkManager.getInstance(context).cancelUniqueWork("reminder_$reminderId")
            Log.d("ReminderWorker", "Cancelled WorkManager reminder $reminderId")
        }
    }

    override suspend fun doWork(): Result {
        val reminderId = inputData.getString(KEY_REMINDER_ID) ?: return Result.failure()
        val eventTitle = inputData.getString(KEY_EVENT_TITLE) ?: "Timeline Event"
        val eventCategory = inputData.getString(KEY_EVENT_CATEGORY) ?: "UNIVERSAL"
        val eventDateStr = inputData.getString(KEY_EVENT_DATE) ?: LocalDate.now().toString()
        val timeDisplay = inputData.getString(KEY_TIME_DISPLAY) ?: ""
        val reminderType = inputData.getString(KEY_REMINDER_TYPE) ?: "START_RELATIVE"

        Log.d("ReminderWorker", "doWork executing for reminderId=$reminderId, title=$eventTitle")

        val channelId = getChannelIdForCategory(eventCategory)
        createNotificationChannel(context, channelId, eventCategory)

        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("target_date", eventDateStr)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.hashCode(),
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = buildString {
            append(eventTitle)
            if (timeDisplay.isNotEmpty()) {
                val action = if (reminderType == "END_RELATIVE") "Ends at" else "Starts at"
                append(" • $action $timeDisplay")
            }
        }

        val colorInt = when (eventCategory.uppercase()) {
            "GOWRI" -> 0xFF4CAF50.toInt()
            "CHANDRASHTAMAM" -> 0xFFE53935.toInt()
            "NAKSHATRA" -> 0xFFFFD700.toInt()
            "MUHURTHAM" -> 0xFF1976D2.toInt()
            "NERAM" -> 0xFFFF9800.toInt()
            "HORA" -> 0xFF9C27B0.toInt()
            "TITHI" -> 0xFF00BCD4.toInt()
            "TAMIL_FESTIVAL" -> 0xFFE91E63.toInt()
            "YOGAM" -> 0xFF3F51B5.toInt()
            "THARA_BALAM" -> 0xFF009688.toInt()
            else -> 0xFF6750A4.toInt()
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Sacred Timeline")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setAutoCancel(false)
            .setColor(colorInt)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(reminderId.hashCode(), notification)
            Log.d("ReminderWorker", "Notification posted successfully for $eventTitle")
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.e("ReminderWorker", "SecurityException posting notification", e)
        }

        // Handle recurring
        val repo = RemindersRepository(context)
        val reminder = repo.getAllReminders().find { it.id == reminderId }
        if (reminder != null && reminder.isRecurring) {
            val (nextDate, nextTrigger) = if (reminder.eventCategory.uppercase() == "TAMIL_FESTIVAL") {
                if (reminder.eventTitle.contains("1st of Every Tamil Month")) {
                    var found = reminder.eventDate.plusDays(1)
                    for (i in 0..35) {
                        val testDate = reminder.eventDate.plusDays(1 + i.toLong())
                        try {
                            if (com.suresh.sacredtimeline.logic.TamilCalendarUtils.getTamilDate(testDate).day == 1) {
                                found = testDate
                                break
                            }
                        } catch (_: Exception) {}
                    }
                    val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(reminder.eventDate, found)
                    Pair(found, reminder.triggerTime.plusSeconds(daysDiff * 86400L))
                } else {
                    val nextYrDate = reminder.eventDate.plusYears(1)
                    Pair(nextYrDate, reminder.triggerTime.plusSeconds(365 * 86400L))
                }
            } else {
                val daysToAdd = when (reminder.eventCategory.uppercase()) {
                    "NAKSHATRA" -> 27
                    "CHANDRASHTAMAM" -> 27
                    else -> 1
                }
                Pair(reminder.eventDate.plusDays(daysToAdd.toLong()), reminder.triggerTime.plusSeconds(daysToAdd.toLong() * 86400L))
            }

            val nextReminder = reminder.copy(
                eventDate = nextDate,
                triggerTime = nextTrigger
            )
            repo.addReminder(nextReminder)
            scheduleReminder(context, nextReminder)
        }

        return Result.success()
    }

    private fun getChannelIdForCategory(category: String): String {
        return when (category.uppercase()) {
            "NAKSHATRA" -> "channel_nakshatra"
            "CHANDRASHTAMAM" -> "channel_chandrashtamam"
            "GOWRI" -> "channel_gowri"
            "MUHURTHAM" -> "channel_muhurtham"
            "NERAM" -> "channel_neram"
            "HORA" -> "channel_hora"
            "TITHI" -> "channel_tithi"
            "TAMIL_FESTIVAL" -> "channel_festival"
            "SOLAR" -> "channel_solar"
            "YOGAM" -> "channel_yogam"
            "THARA_BALAM" -> "channel_tharabalam"
            "CUSTOM" -> "channel_custom"
            else -> "channel_default"
        }
    }

    private fun createNotificationChannel(context: Context, channelId: String, category: String) {
        val channelName = when (channelId) {
            "channel_nakshatra" -> "Nakshatra Reminders"
            "channel_chandrashtamam" -> "Chandrashtamam Warnings"
            "channel_gowri" -> "Gowri Neram Reminders"
            "channel_muhurtham" -> "Muhurtham Reminders"
            "channel_neram" -> "Neram Reminders"
            "channel_hora" -> "Horai Reminders"
            "channel_tithi" -> "Tithi Reminders"
            "channel_festival" -> "Tamil Festival Reminders"
            "channel_solar" -> "Solar & Sun Reminders"
            "channel_yogam" -> "Yogam Reminders"
            "channel_tharabalam" -> "Thara Balam Reminders"
            "channel_custom" -> "Custom Reminders"
            else -> "General Notifications"
        }
        val channelDescription = "Reminders and alerts for $category events"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
            enableVibration(true)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(channel)
    }
}
