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
    fun testRahuKalamForFriday() {
        val sunrise = LocalTime.of(6, 0)
        val sunset = LocalTime.of(18, 0)
        val rahu = PanchangamCalculator.calculateRahuKalam(DayOfWeek.FRIDAY, sunrise, sunset)
        
        assertEquals(LocalTime.of(10, 30), rahu.startTime)
        assertEquals(LocalTime.of(12, 0), rahu.endTime)
    }
}
