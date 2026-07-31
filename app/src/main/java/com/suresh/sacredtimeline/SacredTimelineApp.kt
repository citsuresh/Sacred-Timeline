package com.suresh.sacredtimeline

import android.app.Application
import com.suresh.sacredtimeline.worker.WidgetUpdateWorker

class SacredTimelineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        WidgetUpdateWorker.enqueuePeriodicWork(this)
    }
}
