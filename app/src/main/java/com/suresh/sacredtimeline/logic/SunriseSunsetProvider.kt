package com.suresh.sacredtimeline.logic

import java.time.LocalDate
import java.time.LocalTime

data class SunTimesResult(
    val sunrise: LocalTime,
    val sunset: LocalTime,
    val isFallback: Boolean
)

class SunriseSunsetProvider {

    /**
     * Calculates sunrise and sunset using the local high-precision astronomical engine.
     * No longer relies on external web APIs.
     */
    fun getSunTimes(lat: Double, lng: Double, date: LocalDate, definition: String = "SCIENTIFIC"): SunTimesResult {
        return try {
            // SCIENTIFIC: Top edge of sun (zenith 90.83)
            // TRADITIONAL: Center of sun disk (zenith 90.0)
            val zenith = if (definition == "TRADITIONAL") 90.0 else 90.83
            val times = LunarCalendarUtils.calculateSunriseSunset(lat, lng, date, zenith)
            SunTimesResult(times.first, times.second, false)
        } catch (e: Exception) {
            e.printStackTrace()
            fallback()
        }
    }

    private fun fallback(): SunTimesResult {
        return SunTimesResult(LocalTime.of(6, 0), LocalTime.of(18, 0), true)
    }
}
