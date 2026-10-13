package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.model.BirthProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class LagnaCalculatorTest {

    private val coimbatoreLat = 11.0168
    private val coimbatoreLng = 76.9558
    private val istZone = ZoneId.of("Asia/Kolkata")

    @Test
    fun testSpecificBirthCharts() {
        val profile1 = BirthProfile(
            nameEnglish = "Test 1",
            birthDate = "1983-01-29",
            birthTime = "02:55",
            birthPlace = "Coimbatore",
            latitude = 11.0168,
            longitude = 76.9558,
            timezoneOffsetHours = 5.5
        )
        val result1 = BirthChartCalculator.calculateChart(profile1)
        assertEquals(8, result1.lagnaHouse) // Vrischika (Scorpio)

        val profile2 = BirthProfile(
            nameEnglish = "Test 2",
            birthDate = "1958-03-28",
            birthTime = "09:15",
            birthPlace = "Coimbatore",
            latitude = 11.0168,
            longitude = 76.9558,
            timezoneOffsetHours = 5.5
        )
        val result2 = BirthChartCalculator.calculateChart(profile2)
        assertEquals(2, result2.lagnaHouse) // Rishabha (Taurus)
    }

    @Test
    fun testFeb11_2026_VrishchikaLagna() {
        val date = LocalDate.of(2026, 2, 11)
        val windows = LagnaCalculator.calculateLagnaWindows(date, coimbatoreLat, coimbatoreLng, istZone)
        
        val scorpio = windows.filter { it.rashi == LagnaCalculator.Rashi.VRISHCHIKA }
            .find { it.startTime.isBefore(LocalTime.of(1, 0)) }
        
        assertTrue("Vrishchika Lagna should be present", scorpio != null)
        scorpio?.let {
            assertTimeClose("Start time", LocalTime.of(2, 27), it.startTime, 120)
            assertTimeClose("End time", LocalTime.of(4, 44), it.endTime, 120)
        }
    }

    @Test
    fun testJul24_2026_VrishchikaLagna() {
        val date = LocalDate.of(2026, 7, 24)
        val windows = LagnaCalculator.calculateLagnaWindows(date, coimbatoreLat, coimbatoreLng, istZone)
        
        val scorpio = windows.find { it.rashi == LagnaCalculator.Rashi.VRISHCHIKA && it.startTime.isAfter(LocalTime.NOON) }
        
        assertTrue("Vrishchika Lagna should be present", scorpio != null)
        scorpio?.let {
            assertTimeClose("Start time", LocalTime.of(15, 14), it.startTime, 120)
            assertTimeClose("End time", LocalTime.of(17, 32), it.endTime, 120)
        }
    }

    @Test
    fun testAug04_2026_MeshaLagna() {
        val date = LocalDate.of(2026, 8, 4)
        val windows = LagnaCalculator.calculateLagnaWindows(date, coimbatoreLat, coimbatoreLng, istZone)
        
        val mesha = windows.find { it.rashi == LagnaCalculator.Rashi.MESHA }
        
        assertTrue("Mesha Lagna should be present", mesha != null)
        mesha?.let {
            assertTimeClose("Start time", LocalTime.MIDNIGHT, it.startTime, 10)
        }
    }

    private fun assertTimeClose(label: String, expected: LocalTime, actual: LocalTime, toleranceMinutes: Long = 10) {
        val diff = java.time.Duration.between(expected, actual).abs().toMinutes()
        assertTrue("$label expected $expected but was $actual (diff: $diff min)", diff <= toleranceMinutes)
    }
}
