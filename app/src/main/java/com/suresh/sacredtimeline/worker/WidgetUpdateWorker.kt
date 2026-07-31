package com.suresh.sacredtimeline.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.suresh.sacredtimeline.widget.PanchangamWidget
import java.util.concurrent.TimeUnit

class WidgetUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        PanchangamWidget().updateAll(applicationContext)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "PanchangamWidgetUpdateWorker"

        fun enqueuePeriodicWork(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                30, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
