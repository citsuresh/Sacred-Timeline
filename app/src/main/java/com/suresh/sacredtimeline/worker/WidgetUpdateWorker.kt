package com.suresh.sacredtimeline.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.suresh.sacredtimeline.logic.MockPanchangamProvider
import com.suresh.sacredtimeline.logic.SunriseSunsetProvider
import com.suresh.sacredtimeline.widget.PanchangamWidget
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class WidgetUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // 1. Update the widget immediately
        PanchangamWidget().updateAll(applicationContext)

        // 2. Schedule the next transition-based update
        scheduleNextTransition(applicationContext)

        return Result.success()
    }

    private suspend fun scheduleNextTransition(context: Context) {
        val now = LocalTime.now()
        val date = LocalDate.now()
        val sunProvider = SunriseSunsetProvider()
        val provider = MockPanchangamProvider()

        // Use Coimbatore coords (same as widget default) to find timings
        val sunTimes = sunProvider.getSunTimes(11.0168, 76.9558, date)
        val timings = provider.getTimings(date, sunTimes.sunrise, sunTimes.sunset)

        // Find the earliest endTime that is in the future
        val nextTransition = timings
            .map { it.endTime }
            .filter { it.isAfter(now) }
            .minByOrNull { it }

        if (nextTransition != null) {
            val delay = Duration.between(now, nextTransition).toMillis() + 1000 // +1s buffer
            
            val workRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("TransitionUpdate")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "WidgetTransitionUpdate",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    companion object {
        private const val WORK_NAME = "PanchangamWidgetUpdateWorker"

        fun enqueuePeriodicWork(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
