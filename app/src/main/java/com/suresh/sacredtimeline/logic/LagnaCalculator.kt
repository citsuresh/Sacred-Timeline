package com.suresh.sacredtimeline.logic

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.*

/**
 * High-precision Lagna (Ascendant) Calculator using Meeus astronomical algorithms.
 * Optimized for identifying Mesha (Aries) and Vrishchika (Scorpio) windows
 * for Maitra Muhurtham detection.
 */
object LagnaCalculator {

    enum class Rashi(val value: Int) {
        MESHA(1), VRISHABHA(2), MITHUNA(3), KATAKA(4), SIMHA(5), KANYA(6),
        THULA(7), VRISHCHIKA(8), DHANUS(9), MAKARA(10), KUMBHA(11), MEENA(12)
    }

    data class LagnaWindow(
        val rashi: Rashi,
        val startTime: LocalTime,
        val endTime: LocalTime
    )

    /**
     * Calculates all Lagna windows for a specific date and location.
     */
    fun calculateLagnaWindows(
        date: LocalDate,
        lat: Double,
        lng: Double,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): List<LagnaWindow> {
        val windows = mutableListOf<LagnaWindow>()
        
        // Search window: Pad with 2 hours to capture transitions across midnight
        val startOfSearch = date.atStartOfDay(zoneId).minusHours(2).toInstant()
        val endOfSearch = date.plusDays(1).atStartOfDay(zoneId).plusHours(2).toInstant()
        
        var current = startOfSearch
        var lastRashi: Rashi? = null
        var lastTransitionTime = startOfSearch

        while (current.isBefore(endOfSearch)) {
            val rashi = getLagnaRashi(current, lat, lng)
            if (lastRashi == null) {
                lastRashi = rashi
            } else if (rashi != lastRashi) {
                // Transition found! Refine to within ~15 seconds
                val refinedTransition = findTransition(current.minusSeconds(300), current, lastRashi, lat, lng)
                
                addWindowIfRelevant(windows, lastRashi, lastTransitionTime, refinedTransition, date, zoneId)
                
                lastRashi = rashi
                lastTransitionTime = refinedTransition
            }
            current = current.plusSeconds(300) // 5-minute steps
        }

        // Add the tail window
        lastRashi?.let {
            addWindowIfRelevant(windows, it, lastTransitionTime, endOfSearch, date, zoneId)
        }

        return windows.distinctBy { it.rashi.name + it.startTime.toString() }.sortedBy { it.startTime }
    }

    private fun addWindowIfRelevant(
        windows: MutableList<LagnaWindow>,
        rashi: Rashi,
        startInstant: Instant,
        endInstant: Instant,
        targetDate: LocalDate,
        zoneId: ZoneId
    ) {
        val startZdt = startInstant.atZone(zoneId)
        val endZdt = endInstant.atZone(zoneId)
        
        val dayStart = targetDate.atStartOfDay(zoneId).toInstant()
        val dayEnd = targetDate.plusDays(1).atStartOfDay(zoneId).toInstant()

        // Does the window [startInstant, endInstant] overlap with [dayStart, dayEnd]?
        if (startInstant.isBefore(dayEnd) && endInstant.isAfter(dayStart)) {
            val startTime = if (startInstant.isBefore(dayStart)) LocalTime.MIDNIGHT else startZdt.toLocalTime()
            val endTime = if (endInstant.isAfter(dayEnd)) LocalTime.MAX else endZdt.toLocalTime()

            if (startTime != endTime) {
                windows.add(LagnaWindow(rashi, startTime, endTime))
            }
        }
    }

    private fun getLagnaRashi(instant: Instant, lat: Double, lng: Double): Rashi {
        val jd = (instant.toEpochMilli() / 86400000.0) + 2440587.5
        val t = (jd - 2451545.0) / 36525.0
        
        val epsilon = 23.4392911 - (46.8150 * t) / 3600.0
        
        var gmst = 280.46061837 + 360.98564736629 * (jd - 2451545.0)
        var lst = (gmst + lng) % 360.0
        while (lst < 0) lst += 360.0
        
        val lstRad = Math.toRadians(lst)
        val latRad = Math.toRadians(lat)
        val epsRad = Math.toRadians(epsilon)
        
        val y = cos(lstRad)
        val x = -(sin(lstRad) * cos(epsRad) + tan(latRad) * sin(epsRad))
        
        var sayanaAscendant = Math.toDegrees(atan2(y, x))
        while (sayanaAscendant < 0) sayanaAscendant += 360.0
        while (sayanaAscendant >= 360.0) sayanaAscendant -= 360.0
        
        val ayanamsha = LunarCalendarUtils.calculateLahiriAyanamsha(jd)
        var nirayanaAscendant = (sayanaAscendant - ayanamsha + 360.0) % 360.0
        
        val rashiIndex = (floor(nirayanaAscendant / 30.0).toInt() + 1).coerceIn(1, 12)
        return Rashi.entries.first { it.value == rashiIndex }
    }


    private fun findTransition(
        start: Instant,
        end: Instant,
        startRashi: Rashi,
        lat: Double,
        lng: Double
    ): Instant {
        var low = start.toEpochMilli()
        var high = end.toEpochMilli()
        repeat(11) { // Accuracy within ~8 seconds
            val mid = (low + high) / 2
            if (getLagnaRashi(Instant.ofEpochMilli(mid), lat, lng) == startRashi) {
                low = mid
            } else {
                high = mid
            }
        }
        return Instant.ofEpochMilli(high)
    }
}
