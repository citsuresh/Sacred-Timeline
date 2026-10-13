package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.model.BirthProfile
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.*

object BirthChartCalculator {

    data class ChartResult(
        val rasiPositions: Map<String, Int>,
        val navamsaPositions: Map<String, Int>,
        val lagnaHouse: Int,
        val rasiHouse: Int
    )

    fun calculateChart(profile: BirthProfile): ChartResult {
        val date = try { LocalDate.parse(profile.birthDate) } catch (_: Exception) { LocalDate.of(1995, 6, 15) }
        
        // Robust time parsing supporting both 24-hour (HH:mm) and 12-hour (hh:mm AM/PM) formats
        val time = try {
            LocalTime.parse(profile.birthTime)
        } catch (_: Exception) {
            try {
                LocalTime.parse(profile.birthTime, DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
            } catch (_: Exception) {
                try {
                    LocalTime.parse(profile.birthTime, DateTimeFormatter.ofPattern("h:mm a", Locale.US))
                } catch (_: Exception) {
                    LocalTime.of(6, 0)
                }
            }
        }
        
        // Correctly handle fractional timezone offsets (e.g., 5.5 -> +05:30)
        val totalMinutes = (profile.timezoneOffsetHours * 60).toInt()
        val hours = totalMinutes / 60
        val minutes = abs(totalMinutes % 60)
        val zoneOffset = ZoneOffset.ofHoursMinutes(hours, minutes)
        
        // Self-heal legacy profiles where lat/lng defaulted to Chennai but birthPlace is another city
        var lat = profile.latitude
        var lon = profile.longitude
        if ((lat == 13.0827 && lon == 80.2707) && !profile.birthPlace.lowercase().contains("chennai")) {
            CityDatabase.lookupExact(profile.birthPlace)?.let { coords ->
                lat = coords.first
                lon = coords.second
            }
        }

        val instant = date.atTime(time).atZone(zoneOffset).toInstant()
        val jd = (instant.toEpochMilli() / 86400000.0) + 2440587.5
        val t = (jd - 2451545.0) / 36525.0
        val ayanamsha = LunarCalendarUtils.calculateLahiriAyanamsha(jd)

        // 1. Exact Lagna calculation from LagnaCalculator using resolved coordinates & parsed time
        val lagnaRashi = LagnaCalculator.getLagnaRashi(instant, lat, lon).value

        // 2. Moon Rasi
        val moonRasi = LunarCalendarUtils.getMoonRasi(instant)

        // 3. Sun Rasi
        val sunLong = LunarCalendarUtils.calculateSunLongitudeHighPrecision(t)
        var siderealSun = sunLong - ayanamsha
        while (siderealSun < 0) siderealSun += 360.0
        while (siderealSun >= 360.0) siderealSun -= 360.0
        val sunRasi = (floor(siderealSun / 30.0).toInt() + 1).coerceIn(1, 12)

        // Planetary positions using Meeus mean orbital elements and sidereal shift
        val d = jd - 2451545.0
        fun getPlanetRasi(l0: Double, n: Double): Int {
            var longDeg = (l0 + n * d) % 360.0
            if (longDeg < 0) longDeg += 360.0
            var sidereal = longDeg - ayanamsha
            while (sidereal < 0) sidereal += 360.0
            while (sidereal >= 360.0) sidereal -= 360.0
            return (floor(sidereal / 30.0).toInt() + 1).coerceIn(1, 12)
        }

        val mercuryRasi = getPlanetRasi(252.250906, 4.09233445)
        val venusRasi = getPlanetRasi(181.979801, 1.60213034)
        val marsRasi = getPlanetRasi(355.453322, 0.52403296)
        val jupiterRasi = getPlanetRasi(34.351519, 0.08308530)
        val saturnRasi = getPlanetRasi(50.077444, 0.03345973)
        val rahuRasi = getPlanetRasi(125.04452, -0.0529538)
        val ketuRasi = ((rahuRasi + 6 - 1) % 12) + 1

        val rasiPositions = mapOf(
            "Sun" to sunRasi,
            "Moon" to moonRasi,
            "Mars" to marsRasi,
            "Mercury" to mercuryRasi,
            "Jupiter" to jupiterRasi,
            "Venus" to venusRasi,
            "Saturn" to saturnRasi,
            "Rahu" to rahuRasi,
            "Ketu" to ketuRasi
        )

        // Navamsa positions (D9)
        val navamsaPositions = rasiPositions.mapValues { (_, rasi) ->
            (((rasi - 1) * 3 + ((instant.toEpochMilli() % 3).toInt())) % 12) + 1
        }

        return ChartResult(
            rasiPositions = rasiPositions,
            navamsaPositions = navamsaPositions,
            lagnaHouse = lagnaRashi,
            rasiHouse = moonRasi
        )
    }
}
