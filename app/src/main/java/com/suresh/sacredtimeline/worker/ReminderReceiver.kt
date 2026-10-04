package com.suresh.sacredtimeline.worker

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.suresh.sacredtimeline.MainActivity
import com.suresh.sacredtimeline.data.RemindersRepository
import com.suresh.sacredtimeline.model.Reminder

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_EVENT_TITLE = "extra_event_title"
        const val EXTRA_EVENT_CATEGORY = "extra_event_category"
        const val EXTRA_EVENT_DATE = "extra_event_date"

        fun scheduleReminder(context: Context, reminder: Reminder) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(EXTRA_REMINDER_ID, reminder.id)
                putExtra(EXTRA_EVENT_TITLE, reminder.eventTitle)
                putExtra(EXTRA_EVENT_CATEGORY, reminder.eventCategory)
                putExtra(EXTRA_EVENT_DATE, reminder.eventDate.toString())
            }
            
            val requestCode = reminder.id.hashCode()
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            var triggerMillis = reminder.triggerTime.toEpochMilli()
            if (triggerMillis <= System.currentTimeMillis()) {
                triggerMillis = System.currentTimeMillis() + 3000L // Instant test trigger if time is in past
            }

            Log.d("ReminderReceiver", "Scheduling reminder '${reminder.eventTitle}' for triggerMillis=$triggerMillis (now=${System.currentTimeMillis()})")

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        }

        fun cancelReminder(context: Context, reminderId: String) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(EXTRA_REMINDER_ID) ?: return
        val eventTitle = intent.getStringExtra(EXTRA_EVENT_TITLE) ?: "Timeline Event"
        val eventCategory = intent.getStringExtra(EXTRA_EVENT_CATEGORY) ?: "UNIVERSAL"
        val eventDate = intent.getStringExtra(EXTRA_EVENT_DATE) ?: java.time.LocalDate.now().toString()

        Log.d("ReminderReceiver", "onReceive triggered for reminderId=$reminderId, title=$eventTitle")

        val channelId = getChannelIdForCategory(eventCategory)
        createNotificationChannel(context, channelId, eventCategory)

        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("target_date", eventDate)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.hashCode(),
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Sacred Timeline Reminder")
            .setContentText(eventTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(reminderId.hashCode(), notification)
            Log.d("ReminderReceiver", "Notification posted successfully for $eventTitle")
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.e("ReminderReceiver", "SecurityException posting notification", e)
        }

        val repo = RemindersRepository(context)
        val reminder = repo.getAllReminders().find { it.id == reminderId }
        if (reminder != null && reminder.isRecurring) {
            val daysToAdd = when (reminder.eventCategory.uppercase()) {
                "NAKSHATRA" -> 27
                "CHANDRASHTAMAM" -> 27
                else -> 1
            }
            val nextDate = reminder.eventDate.plusDays(daysToAdd.toLong())
            val nextTrigger = reminder.triggerTime.plusSeconds(daysToAdd.toLong() * 86400L)
            val nextReminder = reminder.copy(
                eventDate = nextDate,
                triggerTime = nextTrigger
            )
            repo.addReminder(nextReminder)
            scheduleReminder(context, nextReminder)
        }
    }

    private fun getChannelIdForCategory(category: String): String {
        return when (category.uppercase()) {
            "NAKSHATRA" -> "channel_nakshatra"
            "CHANDRASHTAMAM" -> "channel_chandrashtamam"
            "GOWRI" -> "channel_gowri"
            "MUHURTHAM", "BRAHMA", "ABHIJIT", "MAITRA" -> "channel_muhurtham"
            else -> "channel_default"
        }
    }

    private fun createNotificationChannel(context: Context, channelId: String, category: String) {
        val channelName = when (channelId) {
            "channel_nakshatra" -> "Nakshatra Reminders"
            "channel_chandrashtamam" -> "Chandrashtamam Warnings"
            "channel_gowri" -> "Gowri Neram Reminders"
            "channel_muhurtham" -> "Muhurtham Reminders"
            else -> "General Timeline Reminders"
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
