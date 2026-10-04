package com.suresh.sacredtimeline

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.suresh.sacredtimeline.worker.WidgetUpdateWorker

class SacredTimelineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initial schedule with default 30 mins (SettingsViewModel will update if changed)
        WidgetUpdateWorker.enqueuePeriodicWork(this, 30L)

        // Pre-create exact 1-to-1 notification channels on app startup
        createAllNotificationChannels(this)
    }

    companion object {
        fun createAllNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
                
                val channels = listOf(
                    Triple("channel_nakshatra", "Nakshatra Reminders", "Reminders for Nakshatra events"),
                    Triple("channel_tithi", "Tithi Reminders", "Reminders for Tithi events"),
                    Triple("channel_chandrashtamam", "Chandrashtamam Warnings", "Warnings for Chandrashtamam events"),
                    Triple("channel_yogam", "Yogam Reminders", "Reminders for Yogam events"),
                    Triple("channel_tharabalam", "Thara Balam Reminders", "Reminders for Thara Balam events"),
                    Triple("channel_muhurtham", "Muhurtham Reminders", "Reminders for Muhurtham events"),
                    Triple("channel_neram", "Neram Reminders", "Reminders for Neram events"),
                    Triple("channel_hora", "Horai Reminders", "Reminders for Horai events"),
                    Triple("channel_gowri", "Gowri Neram Reminders", "Reminders for Gowri Neram events"),
                    Triple("channel_festival", "Tamil Festival Reminders", "Reminders for Tamil Festival events"),
                    Triple("channel_solar", "Solar & Sun Reminders", "Reminders for Sunrise and Sunset events"),
                    Triple("channel_custom", "Custom Reminders", "Reminders for custom user events"),
                    Triple("channel_default", "General Notifications", "General app notifications and non-reminder alerts")
                )

                channels.forEach { (id, name, desc) ->
                    val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                        description = desc
                        enableVibration(true)
                    }
                    notificationManager.createNotificationChannel(channel)
                }
            }
        }
    }
}
