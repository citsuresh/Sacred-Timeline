package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import kotlin.math.*

object LunarCalendarUtils {

    data class LunarInfo(
        val tithi: Int,
        val nakshatra: Int,
        val pakshaResId: Int,
        val pakshaDay: Int,
        val tithiResId: Int,
        val nakshatraResId: Int,
        val tithiEndTime: LocalTime? = null,
        val nakshatraEndTime: LocalTime? = null
    )

    /**
     * Calculates Lunar info and finds exact end times for Tithi and Nakshatra.
     */
    fun getLunarInfo(date: LocalDate): LunarInfo {
        val zoneId = ZoneId.systemDefault()
        
        // Calculate prevailing values at Noon
        val prevailingNoon = getLunarValues(date.atTime(12, 0).atZone(zoneId).toInstant())
        
        // Find end times by searching between Midnight today and Midnight tomorrow
        val startInstant = date.atStartOfDay(zoneId).toInstant()
        val endInstant = date.plusDays(1).atStartOfDay(zoneId).toInstant()
        
        val tithiEnd = findEndTime(startInstant, endInstant, prevailingNoon.first) { getLunarValues(it).first }
        val nakshatraEnd = findEndTime(startInstant, endInstant, prevailingNoon.second) { getLunarValues(it).second }

        val tithi = prevailingNoon.first
        val isValarpirai = tithi <= 15
        
        return LunarInfo(
            tithi = tithi,
            nakshatra = prevailingNoon.second,
            pakshaResId = if (isValarpirai) R.string.paksha_valarpirai else R.string.paksha_theipirai,
            pakshaDay = if (isValarpirai) tithi else tithi - 15,
            tithiResId = getTithiResId(tithi),
            nakshatraResId = getNakshatraResId(prevailingNoon.second),
            tithiEndTime = tithiEnd?.atZone(zoneId)?.toLocalTime(),
            nakshatraEndTime = nakshatraEnd?.atZone(zoneId)?.toLocalTime()
        )
    }

    private fun getLunarValues(instant: java.time.Instant): Pair<Int, Int> {
        val julianDate = (instant.toEpochMilli() / 86400000.0) + 2440587.5
        val t = (julianDate - 2451545.0) / 36525.0

        val sunLong = calculateSunLongitude(julianDate)
        val moonLong = calculateMoonLongitude(t)
        
        var diff = moonLong - sunLong
        if (diff < 0) diff += 360.0
        val tithi = (floor(diff / 12.0).toInt() + 1).coerceIn(1, 30)

        val ayanamsha = 22.466 + 0.01396 * ((julianDate - 2415020.5) / 365.25)
        var siderealMoon = moonLong - ayanamsha
        if (siderealMoon < 0) siderealMoon += 360.0
        val nakshatra = (floor(siderealMoon / (360.0 / 27.0)).toInt() + 1).coerceIn(1, 27)

        return tithi to nakshatra
    }

    private fun findEndTime(
        start: java.time.Instant,
        end: java.time.Instant,
        currentVal: Int,
        getter: (java.time.Instant) -> Int
    ): java.time.Instant? {
        val startVal = getter(start)
        val endVal = getter(end)
        
        // If it changes during the day
        if (startVal == currentVal && endVal != currentVal) {
            var low = start.toEpochMilli()
            var high = end.toEpochMilli()
            repeat(12) { // ~1 min precision
                val mid = (low + high) / 2
                if (getter(java.time.Instant.ofEpochMilli(mid)) == currentVal) {
                    low = mid
                } else {
                    high = mid
                }
            }
            return java.time.Instant.ofEpochMilli(high)
        }
        return null
    }

    fun calculateBrahmaMuhurtham(sunrise: LocalTime): Pair<LocalTime, LocalTime> {
        // Brahma Muhurtham starts 1 hour 36 minutes before sunrise
        val start = sunrise.minusMinutes(96)
        val end = sunrise.minusMinutes(48)
        return start to end
    }

    fun calculateAbhijitMuhurtham(sunrise: LocalTime, sunset: LocalTime): Pair<LocalTime, LocalTime>? {
        // Midday = average of sunrise and sunset
        // Duration between sunrise and sunset
        val totalDayMinutes = java.time.Duration.between(sunrise, sunset).toMinutes()
        if (totalDayMinutes <= 0) return null
        
        val midday = sunrise.plusMinutes(totalDayMinutes / 2)
        val start = midday.minusMinutes(24)
        val end = midday.plusMinutes(24)
        return start to end
    }

    private fun calculateSunLongitude(jd: Double): Double {
        val g = 357.529 + 0.98560028 * (jd - 2451545.0)
        val q = 280.459 + 0.98564736 * (jd - 2451545.0)
        val l = q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g))
        return l % 360.0
    }

    private fun calculateMoonLongitude(t: Double): Double {
        val lPrime = 218.316 + 481267.881 * t
        val mPrime = 134.963 + 477198.867 * t
        val d = 297.850 + 445267.111 * t
        
        var moonLong = lPrime + 6.289 * sin(Math.toRadians(mPrime)) +
                1.274 * sin(Math.toRadians(2 * d - mPrime)) +
                0.658 * sin(Math.toRadians(2 * d)) +
                0.214 * sin(Math.toRadians(2 * mPrime))
        
        return moonLong % 360.0
    }

    private fun getTithiResId(tithi: Int): Int = when (tithi) {
        1 -> R.string.tithi_1
        2 -> R.string.tithi_2
        3 -> R.string.tithi_3
        4 -> R.string.tithi_4
        5 -> R.string.tithi_5
        6 -> R.string.tithi_6
        7 -> R.string.tithi_7
        8 -> R.string.tithi_8
        9 -> R.string.tithi_9
        10 -> R.string.tithi_10
        11 -> R.string.tithi_11
        12 -> R.string.tithi_12
        13 -> R.string.tithi_13
        14 -> R.string.tithi_14
        15 -> R.string.tithi_15
        16 -> R.string.tithi_16
        17 -> R.string.tithi_17
        18 -> R.string.tithi_18
        19 -> R.string.tithi_19
        20 -> R.string.tithi_20
        21 -> R.string.tithi_21
        22 -> R.string.tithi_22
        23 -> R.string.tithi_23
        24 -> R.string.tithi_24
        25 -> R.string.tithi_25
        26 -> R.string.tithi_26
        27 -> R.string.tithi_27
        28 -> R.string.tithi_28
        29 -> R.string.tithi_29
        30 -> R.string.tithi_30
        else -> R.string.tithi_1
    }

    private fun getNakshatraResId(star: Int): Int = when (star) {
        1 -> R.string.star_1
        2 -> R.string.star_2
        3 -> R.string.star_3
        4 -> R.string.star_4
        5 -> R.string.star_5
        6 -> R.string.star_6
        7 -> R.string.star_7
        8 -> R.string.star_8
        9 -> R.string.star_9
        10 -> R.string.star_10
        11 -> R.string.star_11
        12 -> R.string.star_12
        13 -> R.string.star_13
        14 -> R.string.star_14
        15 -> R.string.star_15
        16 -> R.string.star_16
        17 -> R.string.star_17
        18 -> R.string.star_18
        19 -> R.string.star_19
        20 -> R.string.star_20
        21 -> R.string.star_21
        22 -> R.string.star_22
        23 -> R.string.star_23
        24 -> R.string.star_24
        25 -> R.string.star_25
        26 -> R.string.star_26
        27 -> R.string.star_27
        else -> R.string.star_1
    }
}
