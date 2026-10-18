package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.model.BirthProfile
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.math.*

object DasaCalculator {

    data class DasaPeriod(
        val planet: String,
        val totalYears: Double,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val bhuktis: List<BhuktiPeriod>
    )

    data class BhuktiPeriod(
        val planet: String,
        val startDate: LocalDate,
        val endDate: LocalDate
    )

    data class DasaResult(
        val birthNakshatra: Int,
        val birthNakshatraName: String,
        val startingPlanet: String,
        val balanceYearsAtBirth: Double,
        val currentMahadasa: String,
        val currentBhukti: String,
        val allDasas: List<DasaPeriod>
    )

    private val dasaLords = listOf("Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter", "Saturn", "Mercury")
    private val dasaYears = mapOf(
        "Ketu" to 7.0,
        "Venus" to 20.0,
        "Sun" to 6.0,
        "Moon" to 10.0,
        "Mars" to 7.0,
        "Rahu" to 18.0,
        "Jupiter" to 16.0,
        "Saturn" to 19.0,
        "Mercury" to 17.0
    )

    private val nakshatraLords = listOf(
        "Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter", "Saturn", "Mercury",
        "Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter", "Saturn", "Mercury",
        "Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter", "Saturn", "Mercury"
    )

    private val nakshatraNames = listOf(
        "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashirsha", "Ardra", "Punarvasu", "Pushya", "Ashlesha",
        "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha",
        "Mula", "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
    )

    fun calculateVimsottariDasa(profile: BirthProfile, targetDate: LocalDate = LocalDate.now()): DasaResult {
        val date = try { LocalDate.parse(profile.birthDate) } catch (_: Exception) { LocalDate.of(1995, 6, 15) }
        val time = try { LocalTime.parse(profile.birthTime) } catch (_: Exception) { LocalTime.of(6, 0) }
        val totalMinutes = (profile.timezoneOffsetHours * 60).toInt()
        val zoneOffset = ZoneOffset.ofHoursMinutes(totalMinutes / 60, abs(totalMinutes % 60))
        val instant = date.atTime(time).atZone(zoneOffset).toInstant()

        val jd = (instant.toEpochMilli() / 86400000.0) + 2440587.5
        val t = (jd - 2451545.0) / 36525.0
        val ayanamsha = LunarCalendarUtils.calculateLahiriAyanamsha(jd)

        val moonLong = (LunarCalendarUtils.calculateMoonLongitudeHighPrecision(t) - ayanamsha + 360.0) % 360.0
        val nakshatraSpan = 360.0 / 27.0
        val nakshatraIndex = (floor(moonLong / nakshatraSpan).toInt()).coerceIn(0, 26)
        val fractionInNakshatra = (moonLong % nakshatraSpan) / nakshatraSpan

        val startingLord = nakshatraLords[nakshatraIndex]
        val fullStartingLordYears = dasaYears[startingLord] ?: 10.0
        val balanceYears = fullStartingLordYears * (1.0 - fractionInNakshatra)
        val elapsedYears = fullStartingLordYears - balanceYears

        val fullStartingDasaStart = date.minusDays((elapsedYears * 365.25).toLong())
        val startIndex = dasaLords.indexOf(startingLord)

        val allDasas = mutableListOf<DasaPeriod>()

        // 1. Generate full starting Mahadasa and take the remaining slice active from birth onwards
        val fullBhuktis = mutableListOf<BhuktiPeriod>()
        var bStart = fullStartingDasaStart
        for (j in 0 until 9) {
            val bhuktiLordIndex = (startIndex + j) % 9
            val bhuktiLord = dasaLords[bhuktiLordIndex]
            val bhuktiYears = fullStartingLordYears * ((dasaYears[bhuktiLord] ?: 10.0) / 120.0)
            val bhuktiDays = (bhuktiYears * 365.25).toLong()
            val bEnd = bStart.plusDays(bhuktiDays)
            fullBhuktis.add(BhuktiPeriod(bhuktiLord, bStart, bEnd))
            bStart = bEnd
        }

        val startingDasaActualEnd = date.plusDays((balanceYears * 365.25).toLong())
        val startingBhuktis = fullBhuktis.filter { !it.endDate.isBefore(date) && !it.startDate.isAfter(startingDasaActualEnd) }.map { bh ->
            BhuktiPeriod(
                planet = bh.planet,
                startDate = if (bh.startDate.isBefore(date)) date else bh.startDate,
                endDate = if (bh.endDate.isAfter(startingDasaActualEnd)) startingDasaActualEnd else bh.endDate
            )
        }.toMutableList()

        if (startingBhuktis.isNotEmpty()) {
            val firstB = startingBhuktis.first()
            val lastB = startingBhuktis.last()
            startingBhuktis[0] = firstB.copy(startDate = date)
            startingBhuktis[startingBhuktis.size - 1] = lastB.copy(endDate = startingDasaActualEnd)
        }

        allDasas.add(
            DasaPeriod(
                planet = startingLord,
                totalYears = balanceYears,
                startDate = date,
                endDate = startingDasaActualEnd,
                bhuktis = startingBhuktis
            )
        )

        // 2. Generate subsequent Dasas for their full durations
        var currentDate = startingDasaActualEnd
        for (i in 1 until 9) {
            val lordIndex = (startIndex + i) % 9
            val lord = dasaLords[lordIndex]
            val fullYears = dasaYears[lord] ?: 10.0
            val days = (fullYears * 365.25).toLong()
            val endDate = currentDate.plusDays(days)

            val bhuktis = mutableListOf<BhuktiPeriod>()
            var bhuktiStartDate = currentDate
            for (j in 0 until 9) {
                val bhuktiLordIndex = (lordIndex + j) % 9
                val bhuktiLord = dasaLords[bhuktiLordIndex]
                val bhuktiYears = fullYears * ((dasaYears[bhuktiLord] ?: 10.0) / 120.0)
                val bhuktiDays = (bhuktiYears * 365.25).toLong()
                val bhuktiEndDate = bhuktiStartDate.plusDays(bhuktiDays)
                bhuktis.add(BhuktiPeriod(bhuktiLord, bhuktiStartDate, bhuktiEndDate))
                bhuktiStartDate = bhuktiEndDate
            }

            if (bhuktis.isNotEmpty()) {
                val firstB = bhuktis.first()
                val lastB = bhuktis.last()
                bhuktis[0] = firstB.copy(startDate = currentDate)
                bhuktis[bhuktis.size - 1] = lastB.copy(endDate = endDate)
            }

            allDasas.add(DasaPeriod(lord, fullYears, currentDate, endDate, bhuktis))
            currentDate = endDate
        }

        // 3. Ensure total sum of all dasas equals exactly 120.0 years by adjusting the last dasa and its bhuktis
        val totalSoFar = allDasas.sumOf { it.totalYears }
        val correction = 120.0 - totalSoFar
        if (abs(correction) > 0.001 && allDasas.isNotEmpty()) {
            val last = allDasas.last()
            val newTotalYears = (last.totalYears + correction).coerceAtLeast(0.1)
            val newDays = (newTotalYears * 365.25).toLong()
            val newEndDate = last.startDate.plusDays(newDays)

            val fullLordYears = dasaYears[last.planet] ?: 10.0
            val lastStartIndex = dasaLords.indexOf(last.planet)
            val bhuktis = mutableListOf<BhuktiPeriod>()
            var bhuktiStartDate = last.startDate
            for (j in 0 until 9) {
                val bhuktiLordIndex = (lastStartIndex + j) % 9
                val bhuktiLord = dasaLords[bhuktiLordIndex]
                val bhuktiYears = newTotalYears * ((dasaYears[bhuktiLord] ?: 10.0) / fullLordYears)
                val bhuktiDays = (bhuktiYears * 365.25).toLong()
                val bhuktiEndDate = bhuktiStartDate.plusDays(bhuktiDays)
                bhuktis.add(BhuktiPeriod(bhuktiLord, bhuktiStartDate, bhuktiEndDate))
                bhuktiStartDate = bhuktiEndDate
            }
            if (bhuktis.isNotEmpty()) {
                val firstB = bhuktis.first()
                val lastB = bhuktis.last()
                bhuktis[0] = firstB.copy(startDate = last.startDate)
                bhuktis[bhuktis.size - 1] = lastB.copy(endDate = newEndDate)
            }

            allDasas[allDasas.size - 1] = DasaPeriod(
                planet = last.planet,
                totalYears = newTotalYears,
                startDate = last.startDate,
                endDate = newEndDate,
                bhuktis = bhuktis
            )
        }

        var currentDasa = startingLord
        var currentBhukti = startingLord
        for (dasa in allDasas) {
            if (!targetDate.isBefore(dasa.startDate) && targetDate.isBefore(dasa.endDate)) {
                currentDasa = dasa.planet
                for (bhukti in dasa.bhuktis) {
                    if (!targetDate.isBefore(bhukti.startDate) && targetDate.isBefore(bhukti.endDate)) {
                        currentBhukti = bhukti.planet
                        break
                    }
                }
                break
            }
        }

        return DasaResult(
            birthNakshatra = nakshatraIndex + 1,
            birthNakshatraName = nakshatraNames[nakshatraIndex % 27],
            startingPlanet = startingLord,
            balanceYearsAtBirth = balanceYears,
            currentMahadasa = currentDasa,
            currentBhukti = currentBhukti,
            allDasas = allDasas
        )
    }
}
