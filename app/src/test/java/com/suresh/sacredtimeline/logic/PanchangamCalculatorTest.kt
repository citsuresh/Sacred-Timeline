package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.model.Auspiciousness
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime

class PanchangamCalculatorTest {

    @Test
    fun testNallaNeramForFridayJuly31_2026() {
        val sunrise = LocalTime.of(6, 0)
        val sunset = LocalTime.of(18, 0) // Simplified for standard 12h day
        val dayOfWeek = DayOfWeek.FRIDAY
        
        val nallaNeram = PanchangamCalculator.calculateNallaNeram(dayOfWeek, sunrise, sunset)
        
        // Expected: 09:15 - 10:15 and 16:45 - 17:45
        assertEquals("Should have 2 Nalla Neram slots", 2, nallaNeram.size)
        
        val firstSlot = nallaNeram[0]
        assertEquals(LocalTime.of(9, 15), firstSlot.startTime)
        assertEquals(LocalTime.of(10, 15), firstSlot.endTime)
        assertEquals(Auspiciousness.GREEN, firstSlot.auspiciousness)
        
        val secondSlot = nallaNeram[1]
        assertEquals(LocalTime.of(16, 45), secondSlot.startTime)
        assertEquals(LocalTime.of(17, 45), secondSlot.endTime)
        assertEquals(Auspiciousness.GREEN, secondSlot.auspiciousness)
    }

    @Test
    fun testSpecialPeriodsForFriday() {
        val sunrise = LocalTime.of(6, 0)
        val sunset = LocalTime.of(18, 0)
        val dayOfWeek = DayOfWeek.FRIDAY
        
        val rahu = PanchangamCalculator.calculateRahuKalam(dayOfWeek, sunrise, sunset)
        assertEquals("Rahu", rahu.name)
        assertEquals(LocalTime.of(10, 30), rahu.startTime)
        assertEquals(LocalTime.of(12, 0), rahu.endTime)
        assertEquals(Auspiciousness.DARK_RED, rahu.auspiciousness)
        
        val yama = PanchangamCalculator.calculateYamagandam(dayOfWeek, sunrise, sunset)
        assertEquals("Yama", yama.name)
        assertEquals(LocalTime.of(15, 0), yama.startTime)
        assertEquals(LocalTime.of(16, 30), yama.endTime)
        assertEquals(Auspiciousness.ORANGE, yama.auspiciousness)
        
        val kuli = PanchangamCalculator.calculateKuligai(dayOfWeek, sunrise, sunset)
        assertEquals("Kuli", kuli.name)
        assertEquals(LocalTime.of(7, 30), kuli.startTime)
        assertEquals(LocalTime.of(9, 0), kuli.endTime)
        assertEquals(Auspiciousness.GREY, kuli.auspiciousness)
    }
}
