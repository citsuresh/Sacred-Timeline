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
        // Trigger an immediate update via WorkManager (Expedited)
        WidgetUpdateWorker.triggerImmediateUpdate(context)
    }
}
