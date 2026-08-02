package com.suresh.sacredtimeline

import android.app.Application
import com.suresh.sacredtimeline.worker.WidgetUpdateWorker

class SacredTimelineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initial schedule with default 30 mins (SettingsViewModel will update if changed)
        WidgetUpdateWorker.enqueuePeriodicWork(this, 30L)
    }
}
