package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.Instant
import java.time.Duration
import kotlin.math.*

object LunarCalendarUtils {

    data class LunarInfo(
        val tithi: Int,
        val nakshatra: Int,
        val pakshaResId: Int,
        val pakshaDay: Int,
        val tithiResId: Int,
        val nakshatraResId: Int
    )

    data class LunarInterval(
        val value: Int,
        val resId: Int,
        val startTime: Instant?,
        val endTime: Instant?
    )

    data class LunarDayInfo(
        val tithis: List<LunarInterval>,
        val nakshatras: List<LunarInterval>,
        val pakshaResId: Int,
        val pakshaDay: Int
    )

    fun getLunarDayInfo(date: LocalDate): LunarDayInfo {
        val zoneId = ZoneId.systemDefault()
        val dayStart = date.atStartOfDay(zoneId).toInstant()
        val dayEnd = date.plusDays(1).atStartOfDay(zoneId).toInstant()

        val tithis = findIntervalsForDay(dayStart, dayEnd) { getLunarValues(it).first }
            .map { it.copy(resId = getTithiResId(it.value)) }
            
        val nakshatras = findIntervalsForDay(dayStart, dayEnd) { getLunarValues(it).second }
            .map { it.copy(resId = getNakshatraResId(it.value)) }

        val noonValue = getLunarValues(date.atTime(12, 0).atZone(zoneId).toInstant()).first
        val isValarpirai = noonValue <= 15

        return LunarDayInfo(
            tithis = tithis,
            nakshatras = nakshatras,
            pakshaResId = if (isValarpirai) R.string.paksha_valarpirai else R.string.paksha_theipirai,
            pakshaDay = if (isValarpirai) noonValue else noonValue - 15
        )
    }

    private fun findIntervalsForDay(
        dayStart: Instant,
        dayEnd: Instant,
        getter: (Instant) -> Int
    ): List<LunarInterval> {
        val intervals = mutableListOf<LunarInterval>()
        var currentSearchStart = dayStart
        
        while (currentSearchStart.isBefore(dayEnd)) {
            val currentVal = getter(currentSearchStart)
            val windowStart = currentSearchStart.minus(Duration.ofDays(2))
            val absoluteStart = findStartTime(windowStart, currentSearchStart, currentVal, getter)
            val windowEnd = currentSearchStart.plus(Duration.ofDays(2))
            val absoluteEnd = findEndTime(currentSearchStart, windowEnd, currentVal, getter)
            
            intervals.add(
                LunarInterval(
                    value = currentVal,
                    resId = 0,
                    startTime = absoluteStart,
                    endTime = absoluteEnd
                )
            )
            
            if (absoluteEnd != null && absoluteEnd.isBefore(dayEnd)) {
                currentSearchStart = absoluteEnd.plusMillis(1000)
            } else {
                break
            }
        }
        return intervals
    }

    private fun getLunarValues(instant: Instant): Pair<Int, Int> {
        val jd = (instant.toEpochMilli() / 86400000.0) + 2440587.5
        val t = (jd - 2451545.0) / 36525.0

        val sunLong = calculateSunLongitudeHighPrecision(t)
        val moonLong = calculateMoonLongitudeHighPrecision(t)
        
        var diff = moonLong - sunLong
        while (diff < 0) diff += 360.0
        while (diff >= 360.0) diff -= 360.0
        val tithi = (floor(diff / 12.0).toInt() + 1).coerceIn(1, 30)

        val ayanamsha = calculateLahiriAyanamsha(jd)
        var siderealMoon = moonLong - ayanamsha
        while (siderealMoon < 0) siderealMoon += 360.0
        while (siderealMoon >= 360.0) siderealMoon -= 360.0
        val nakshatra = (floor(siderealMoon / (360.0 / 27.0)).toInt() + 1).coerceIn(1, 27)

        return tithi to nakshatra
    }

    private fun calculateSunLongitudeHighPrecision(t: Double): Double {
        val l0 = 280.46646 + 36000.76983 * t + 0.0003032 * t * t
        val m = 357.52911 + 35999.05029 * t - 0.0001537 * t * t
        val c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(Math.toRadians(m)) +
                (0.019993 - 0.000101 * t) * sin(Math.toRadians(2 * m)) +
                0.000289 * sin(Math.toRadians(3 * m))
        var sunLong = l0 + c
        while (sunLong < 0) sunLong += 360.0
        while (sunLong >= 360.0) sunLong -= 360.0
        return sunLong
    }

    private fun calculateMoonLongitudeHighPrecision(t: Double): Double {
        // ELP-2000 Main Perturbations
        val lPrime = 218.3164477 + 481267.8812307 * t - 0.0015786 * t * t + t * t * t / 538841.0
        val d = 297.8501921 + 445267.1114034 * t - 0.0018819 * t * t + t * t * t / 545868.0
        val m = 357.5291092 + 35999.0502909 * t - 0.0001536 * t * t + t * t * t / 24490000.0
        val mPrime = 134.9633964 + 477198.8675055 * t + 0.0087414 * t * t + t * t * t / 69699.0
        val f = 93.2720950 + 483202.0175233 * t - 0.0036539 * t * t - t * t * t / 3526000.0

        val dR = Math.toRadians(d)
        val mR = Math.toRadians(m)
        val mPR = Math.toRadians(mPrime)
        val fR = Math.toRadians(f)

        var sumL = 0.0
        // Major terms from Meeus
        sumL += 6288774 * sin(mPR)
        sumL += 1274027 * sin(2 * dR - mPR)
        sumL += 658314 * sin(2 * dR)
        sumL += 213618 * sin(2 * mPR)
        sumL += -185116 * sin(mR)
        sumL += -114332 * sin(2 * fR)
        sumL += 58793 * sin(2 * dR - 2 * mPR)
        sumL += 57066 * sin(2 * dR - mR - mPR)
        sumL += 53322 * sin(2 * dR + mPR)
        sumL += 45758 * sin(2 * dR - mR)
        sumL += -40923 * sin(mR - mPR)
        sumL += -34720 * sin(dR)
        sumL += -30383 * sin(mR + mPR)
        sumL += 15327 * sin(2 * dR - 2 * fR)
        sumL += -12528 * sin(mPR + 2 * fR)
        sumL += 10980 * sin(mPR - 2 * fR)
        sumL += 10675 * sin(4 * dR - mPR)
        sumL += 10034 * sin(4 * dR)
        sumL += 8548 * sin(4 * dR - 2 * mPR)
        sumL += -7888 * sin(2 * dR + mR - mPR)
        sumL += -6766 * sin(2 * dR + mR)
        sumL += -5163 * sin(dR - mPR)
        sumL += 4987 * sin(dR + mR)
        sumL += 4036 * sin(2 * dR - mR + mPR)
        sumL += 3994 * sin(2 * dR + 2 * mPR)
        sumL += 3861 * sin(4 * dR - mR - mPR)
        sumL += 3665 * sin(2 * dR - 3 * mPR)
        sumL += -2689 * sin(mR - 2 * mPR)
        sumL += -2602 * sin(2 * dR - mR + 2 * fR)
        sumL += 2390 * sin(2 * dR - mR - 2 * fR)
        sumL += -2125 * sin(2 * mPR + 2 * fR)
        sumL += 2079 * sin(2 * dR + mR + mPR)
        sumL += 2059 * sin(2 * dR - mR - mPR)

        var moonLong = lPrime + sumL / 1000000.0
        while (moonLong < 0) moonLong += 360.0
        while (moonLong >= 360.0) moonLong -= 360.0
        return moonLong
    }

    fun calculateLahiriAyanamsha(jd: Double): Double {
        // Lahiri Ayanamsha: 22.466... formula is too simple.
        // Better approximation for Lahiri: 23.85 + (jd - 2433282.5) * 0.01396 / 365.25
        // Chitra Paksha (Lahiri) for Jan 1 1900 was 22.4666 deg
        val t = (jd - 2451545.0) / 36525.0
        return 23.852777 + 1.39697127 * t + 0.0003086 * t * t
    }

    fun calculateSunriseSunset(lat: Double, lng: Double, date: LocalDate, zenith: Double = 90.83): Pair<LocalTime, LocalTime> {
        val zoneId = ZoneId.systemDefault()
        val zonedDateTime = date.atStartOfDay(zoneId)
        val jd = (zonedDateTime.toInstant().toEpochMilli() / 86400000.0) + 2440587.5
        
        val t = (jd - 2451545.0) / 36525.0
        val l0 = 280.46646 + 36000.76983 * t
        val m = 357.52911 + 35999.05029 * t
        val e = 0.016708634 - 0.000042037 * t
        val c = (1.914602 - 0.004817 * t) * sin(Math.toRadians(m)) +
                (0.019993 - 0.000101 * t) * sin(Math.toRadians(2 * m))
        val sunLong = l0 + c
        val epsilon = 23.439291 - 0.0130041 * t
        
        val alpha = Math.toDegrees(atan2(cos(Math.toRadians(epsilon)) * sin(Math.toRadians(sunLong)), cos(Math.toRadians(sunLong))))
        val delta = Math.toDegrees(asin(sin(Math.toRadians(epsilon)) * sin(Math.toRadians(sunLong))))
        
        val h0 = Math.toDegrees(acos((cos(Math.toRadians(zenith)) - sin(Math.toRadians(lat)) * sin(Math.toRadians(delta))) / (cos(Math.toRadians(lat)) * cos(Math.toRadians(delta)))))
        
        val equationOfTime = 4 * (l0 - alpha) // Simplified
        val centerNoon = 720 - 4 * lng - equationOfTime
        
        val sunriseMinutes = centerNoon - h0 * 4
        val sunsetMinutes = centerNoon + h0 * 4
        
        val offsetMinutes = zoneId.rules.getOffset(Instant.now()).totalSeconds / 60
        val localSunrise = sunriseMinutes + offsetMinutes
        val localSunset = sunsetMinutes + offsetMinutes
        
        return minutesToLocalTime(localSunrise) to minutesToLocalTime(localSunset)
    }

    private fun minutesToLocalTime(minutes: Double): LocalTime {
        var m = minutes
        while (m < 0) m += 1440
        while (m >= 1440) m -= 1440
        val h = (m / 60).toInt()
        val min = (m % 60).toInt()
        return LocalTime.of(h % 24, min % 60)
    }

    fun calculateBrahmaMuhurtham(sunrise: LocalTime): Pair<LocalTime, LocalTime> {
        val start = sunrise.minusMinutes(96)
        val end = sunrise.minusMinutes(48)
        return start to end
    }

    fun calculateAbhijitMuhurtham(sunrise: LocalTime, sunset: LocalTime): Pair<LocalTime, LocalTime>? {
        val totalDayMinutes = Duration.between(sunrise, sunset).toMinutes()
        if (totalDayMinutes <= 0) return null
        val midday = sunrise.plusMinutes(totalDayMinutes / 2)
        val start = midday.minusMinutes(24)
        val end = midday.plusMinutes(24)
        return start to end
    }

    private fun findStartTime(
        windowStart: Instant,
        current: Instant,
        currentVal: Int,
        getter: (Instant) -> Int
    ): Instant? {
        val startVal = getter(windowStart)
        if (startVal == currentVal) return null
        var low = windowStart.toEpochMilli()
        var high = current.toEpochMilli()
        repeat(20) {
            val mid = (low + high) / 2
            if (getter(Instant.ofEpochMilli(mid)) != currentVal) {
                low = mid
            } else {
                high = mid
            }
        }
        return Instant.ofEpochMilli(high)
    }

    private fun findEndTime(
        current: Instant,
        windowEnd: Instant,
        currentVal: Int,
        getter: (Instant) -> Int
    ): Instant? {
        val endVal = getter(windowEnd)
        if (endVal == currentVal) return null
        var low = current.toEpochMilli()
        var high = windowEnd.toEpochMilli()
        repeat(20) {
            val mid = (low + high) / 2
            if (getter(Instant.ofEpochMilli(mid)) == currentVal) {
                low = mid
            } else {
                high = mid
            }
        }
        return Instant.ofEpochMilli(high)
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
