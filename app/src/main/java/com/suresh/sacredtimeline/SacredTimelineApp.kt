package com.suresh.sacredtimeline

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.suresh.sacredtimeline.data.CacheManager
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.logic.DayDataProvider
import com.suresh.sacredtimeline.worker.WidgetUpdateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

class SacredTimelineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initial schedule with default 15 mins (SettingsViewModel will update if changed)
        WidgetUpdateWorker.enqueuePeriodicWork(this, 15L)
        WidgetUpdateWorker.triggerImmediateUpdate(this)
        com.suresh.sacredtimeline.worker.WatchdogWorker.enqueuePeriodicWork(this)

        // Pre-create exact 1-to-1 notification channels on app startup
        createAllNotificationChannels(this)

        // Pre-warm cache for today on app startup so widgets load instantly with 0ms delay
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = SettingsRepository(applicationContext)
                val cacheManager = CacheManager(applicationContext)
                val mode = repository.locationMode.firstOrNull() ?: "AUTO"
                val lat = if (mode == "AUTO") {
                    repository.lastKnownLatitude.firstOrNull() ?: 13.0827
                } else {
                    repository.manualLatitude.firstOrNull() ?: 13.0827
                }
                val lng = if (mode == "AUTO") {
                    repository.lastKnownLongitude.firstOrNull() ?: 80.2707
                } else {
                    repository.manualLongitude.firstOrNull() ?: 80.2707
                }
                val today = LocalDate.now()
                val cache = cacheManager.loadCache(lat, lng)
                if (cache?.containsKey(today) != true) {
                    val dayData = DayDataProvider(applicationContext).fetchDayData(today, lat, lng, usePrimaryProfile = true)
                    val newCache = mutableMapOf(today to dayData)
                    cacheManager.saveCache(lat, lng, newCache)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
                    Triple("channel_default", "General Notifications", "General app notifications and non-reminder alerts"),
                    Triple("channel_watchdog", "Watchdog & Health Alerts", "Alerts and recovery notifications for background components")
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
