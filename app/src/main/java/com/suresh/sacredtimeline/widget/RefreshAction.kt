package com.suresh.sacredtimeline.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.suresh.sacredtimeline.worker.WidgetUpdateWorker

class RefreshActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Instant update for the current widget instance
        PanchangamWidget().update(context, glanceId)
        
        // Also trigger the background worker to ensure all widgets and cache are synchronized
        WidgetUpdateWorker.triggerImmediateUpdate(context)
    }
}
