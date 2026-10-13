package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.model.BirthProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.temporal.ChronoUnit

class DasaCalculatorTest {

    @Test
    fun testVimsottariDasaTotalSpan120YearsAndNoGaps() {
        val profile = BirthProfile(
            nameEnglish = "Suresh Kumar V",
            birthDate = "1983-01-29",
            birthTime = "02:55",
            birthPlace = "Coimbatore",
            latitude = 11.0168,
            longitude = 76.9558,
            timezoneOffsetHours = 5.5
        )

        val result = DasaCalculator.calculateVimsottariDasa(profile)

        // 1. Verify 9 dasas generated
        assertEquals(9, result.allDasas.size)

        // 2. Verify start of first dasa is birth date
        assertEquals(profile.birthDate, result.allDasas.first().startDate.toString())

        // 3. Verify final dasa ends at 120 years mark (+/- 5 days leap tolerance)
        val birthDate = java.time.LocalDate.parse(profile.birthDate)
        val expectedEnd = birthDate.plusYears(120)
        val actualEnd = result.allDasas.last().endDate
        val daysDiff = Math.abs(ChronoUnit.DAYS.between(expectedEnd, actualEnd))
        assertTrue("Final dasa should end at the 120-year mark (diff: $daysDiff days)", daysDiff <= 5)

        // 4. Verify total sum of all dasa years equals 120.0 years
        val totalYearsSum = result.allDasas.sumOf { it.totalYears }
        assertTrue("Total dasa years sum should be 120.0 (was $totalYearsSum)", Math.abs(totalYearsSum - 120.0) < 0.01)

        // 5. Verify NO GAPS and NO OVERLAPS between consecutive Dasas
        for (i in 0 until result.allDasas.size - 1) {
            val current = result.allDasas[i]
            val next = result.allDasas[i + 1]
            assertEquals("Dasa transition gap between ${current.planet} and ${next.planet}", current.endDate, next.startDate)
        }

        // 6. Verify NO GAPS and NO OVERLAPS between consecutive Bhuktis within each Dasa
        result.allDasas.forEach { dasa ->
            assertTrue("Dasa ${dasa.planet} must have bhuktis", dasa.bhuktis.isNotEmpty())
            for (j in 0 until dasa.bhuktis.size - 1) {
                val bCur = dasa.bhuktis[j]
                val bNext = dasa.bhuktis[j + 1]
                assertEquals("Bhukti transition gap in ${dasa.planet} dasa between ${bCur.planet} and ${bNext.planet}", bCur.endDate, bNext.startDate)
            }
            // Verify first bhukti starts at dasa start and last bhukti ends at dasa end
            assertEquals("First bhukti must start at dasa start in ${dasa.planet}", dasa.startDate, dasa.bhuktis.first().startDate)
            assertEquals("Last bhukti must end at dasa end in ${dasa.planet}", dasa.endDate, dasa.bhuktis.last().endDate)
        }
    }

    @Test
    fun testMultipleProfilesVimsottariContinuity() {
        val profiles = listOf(
            BirthProfile(nameEnglish = "P1", birthDate = "1995-06-15", birthTime = "12:00", birthPlace = "Chennai", latitude = 13.0827, longitude = 80.2707, timezoneOffsetHours = 5.5),
            BirthProfile(nameEnglish = "P2", birthDate = "2000-12-01", birthTime = "06:30", birthPlace = "Madurai", latitude = 9.9252, longitude = 78.1198, timezoneOffsetHours = 5.5)
        )

        profiles.forEach { profile ->
            val result = DasaCalculator.calculateVimsottariDasa(profile)
            assertEquals(9, result.allDasas.size)
            val totalYearsSum = result.allDasas.sumOf { it.totalYears }
            assertTrue("Total dasa years sum for ${profile.nameEnglish} should be 120.0", Math.abs(totalYearsSum - 120.0) < 0.01)

            // Check no gaps between dasas
            for (i in 0 until result.allDasas.size - 1) {
                assertEquals(result.allDasas[i].endDate, result.allDasas[i + 1].startDate)
            }
        }
    }
}
