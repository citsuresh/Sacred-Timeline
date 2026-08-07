package com.suresh.sacredtimeline.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.suresh.sacredtimeline.data.CacheManager
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.logic.MockPanchangamProvider
import com.suresh.sacredtimeline.logic.SunriseSunsetProvider
import com.suresh.sacredtimeline.model.DayData
import com.suresh.sacredtimeline.widget.PanchangamWidget
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class WidgetUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val repository = SettingsRepository(applicationContext)
            val cacheManager = CacheManager(applicationContext)
            
            // 1. Fetch current settings for location
            val mode = repository.locationMode.first()
            val lat: Double
            val lng: Double
            
            if (mode == "AUTO") {
                lat = repository.lastKnownLatitude.first()
                lng = repository.lastKnownLongitude.first()
            } else {
                lat = repository.manualLatitude.first()
                lng = repository.manualLongitude.first()
            }

            // 2. Check Cache / Maintenance
            val cache = cacheManager.loadCache(lat, lng)
            val today = LocalDate.now()
            
            // Maintenance: If cache is missing today or low on future days, refill it
            val hasToday = cache?.containsKey(today) == true
            val futureDays = cache?.keys?.count { it.isAfter(today) } ?: 0
            
            if (!hasToday || futureDays < 2) {
                refillCache(lat, lng, today, repository, cacheManager)
            }

            // 3. Update the widget (It will read from the same cache)
            PanchangamWidget().updateAll(applicationContext)

            // 4. Schedule next transition
            scheduleNextTransition(applicationContext, lat, lng)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun fetchDayData(lat: Double, lng: Double, date: LocalDate, repository: SettingsRepository): DayData {
        val sunProvider = SunriseSunsetProvider()
        val provider = MockPanchangamProvider()
        
        val sunDef = repository.sunriseDefinition.first()
        val style = repository.specialPeriodStyle.first()
        
        val sunResult = sunProvider.getSunTimes(lat, lng, date, sunDef)
        val timings = provider.getTimings(date, sunResult.sunrise, sunResult.sunset, style, sunDef, lat, lng)
        
        return DayData(
            nallaNeram = timings.filterIsInstance<com.suresh.sacredtimeline.model.NallaNeram>(),
            gowriNeram = timings.filterIsInstance<com.suresh.sacredtimeline.model.GowriNeram>(),
            hora = timings.filterIsInstance<com.suresh.sacredtimeline.model.Hora>(),
            specialPeriods = timings.filterIsInstance<com.suresh.sacredtimeline.model.SpecialPeriod>(),
            sunrise = sunResult.sunrise,
            sunset = sunResult.sunset,
            isFallback = sunResult.isFallback,
            brahmaMuhurtham = com.suresh.sacredtimeline.model.Muhurtham(
                "Brahma Muhurtham", "",
                com.suresh.sacredtimeline.logic.LunarCalendarUtils.calculateBrahmaMuhurtham(sunResult.sunrise).first,
                com.suresh.sacredtimeline.logic.LunarCalendarUtils.calculateBrahmaMuhurtham(sunResult.sunrise).second,
                com.suresh.sacredtimeline.model.Auspiciousness.GREEN
            ),
            abhijitMuhurtham = com.suresh.sacredtimeline.logic.LunarCalendarUtils.calculateAbhijitMuhurtham(sunResult.sunrise, sunResult.sunset)?.let {
                com.suresh.sacredtimeline.model.Muhurtham("Abhijit Muhurtham", "", it.first, it.second, com.suresh.sacredtimeline.model.Auspiciousness.GREEN)
            }
        )
    }

    private suspend fun refillCache(lat: Double, lng: Double, centerDate: LocalDate, repository: SettingsRepository, cacheManager: CacheManager) {
        val rangeDays = repository.preloadDays.first()
        val newCache = mutableMapOf<LocalDate, DayData>()
        
        // Load existing to merge
        cacheManager.loadCache(lat, lng)?.let { newCache.putAll(it) }

        val datesToLoad = (-rangeDays..rangeDays).map { centerDate.plusDays(it.toLong()) }
        datesToLoad.forEach { date ->
            if (!newCache.containsKey(date)) {
                newCache[date] = fetchDayData(lat, lng, date, repository)
            }
        }
        
        // Cleanup old days
        val cutoff = centerDate.minusDays(rangeDays.toLong() + 1)
        newCache.keys.removeIf { it.isBefore(cutoff) }

        cacheManager.saveCache(lat, lng, newCache)
    }

    private suspend fun scheduleNextTransition(context: Context, lat: Double, lng: Double) {
        val now = LocalTime.now()
        val date = LocalDate.now()
        val sunProvider = SunriseSunsetProvider()
        val provider = MockPanchangamProvider()
        val repository = SettingsRepository(context)

        val sunDef = repository.sunriseDefinition.first()
        val style = repository.specialPeriodStyle.first()

        val sunTimes = sunProvider.getSunTimes(lat, lng, date, sunDef)
        val timings = provider.getTimings(date, sunTimes.sunrise, sunTimes.sunset, style, sunDef, lat, lng)
        
        val brahma = com.suresh.sacredtimeline.logic.LunarCalendarUtils.calculateBrahmaMuhurtham(sunTimes.sunrise)
        val abhijit = com.suresh.sacredtimeline.logic.LunarCalendarUtils.calculateAbhijitMuhurtham(sunTimes.sunrise, sunTimes.sunset)

        // Find the earliest endTime that is in the future across ALL possible blocks
        val allEndTimes = timings.map { it.endTime }.toMutableList()
        allEndTimes.add(brahma.second)
        abhijit?.let { allEndTimes.add(it.second) }
        
        val nextTransition = allEndTimes
            .filter { it.isAfter(now) }
            .minByOrNull { it }

        if (nextTransition != null) {
            val delay = Duration.between(now, nextTransition).toMillis() + 1000 // +1s buffer
            
            val workRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
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
        private const val IMMEDIATE_WORK_NAME = "ImmediateWidgetUpdate"

        fun triggerImmediateUpdate(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag("ImmediateUpdate")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                IMMEDIATE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun enqueuePeriodicWork(context: Context, intervalMinutes: Long = 30) {
            val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                intervalMinutes, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }
}
