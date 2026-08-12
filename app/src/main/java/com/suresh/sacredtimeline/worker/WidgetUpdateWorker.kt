package com.suresh.sacredtimeline.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.suresh.sacredtimeline.data.CacheManager
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.logic.DayDataProvider
import com.suresh.sacredtimeline.model.DayData
import com.suresh.sacredtimeline.widget.PanchangamWidget
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
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
        return DayDataProvider(applicationContext).fetchDayData(date, lat, lng)
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
        val todayData = DayDataProvider(context).fetchDayData(date, lat, lng)

        // Find all transition boundaries (start and end times) to ensure 100% update accuracy
        val allBoundaries = mutableSetOf<LocalTime>()
        
        // Collate all boundaries from the unified DayData
        todayData.nallaNeram.forEach { allBoundaries.add(it.startTime); allBoundaries.add(it.endTime) }
        todayData.gowriNeram.forEach { allBoundaries.add(it.startTime); allBoundaries.add(it.endTime) }
        todayData.hora.forEach { allBoundaries.add(it.startTime); allBoundaries.add(it.endTime) }
        todayData.specialPeriods.forEach { allBoundaries.add(it.startTime); allBoundaries.add(it.endTime) }
        
        todayData.brahmaMuhurtham?.let { allBoundaries.add(it.startTime); allBoundaries.add(it.endTime) }
        todayData.abhijitMuhurtham?.let { allBoundaries.add(it.startTime); allBoundaries.add(it.endTime) }
        todayData.maitraMuhurtham.forEach { allBoundaries.add(it.startTime); allBoundaries.add(it.endTime) }
        
        // Fix: Also monitor Tithi and Nakshatra boundaries for header accuracy
        val zoneId = ZoneId.systemDefault()
        todayData.tithis.forEach { it.startTime?.let { s -> allBoundaries.add(s.atZone(zoneId).toLocalTime()) }; it.endTime?.let { e -> allBoundaries.add(e.atZone(zoneId).toLocalTime()) } }
        todayData.nakshatras.forEach { it.startTime?.let { s -> allBoundaries.add(s.atZone(zoneId).toLocalTime()) }; it.endTime?.let { e -> allBoundaries.add(e.atZone(zoneId).toLocalTime()) } }
        
        val nextTransition = allBoundaries
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
