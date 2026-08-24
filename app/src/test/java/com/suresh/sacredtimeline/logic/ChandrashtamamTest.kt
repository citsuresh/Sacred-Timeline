package com.suresh.sacredtimeline.logic

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class ChandrashtamamTest {

    @Test
    fun testBirthRasiMapping() {
        // Simple stars
        assertEquals(1, LunarCalendarUtils.getBirthRasi("STAR_1")) // Ashwini -> Mesham
        assertEquals(2, LunarCalendarUtils.getBirthRasi("STAR_4")) // Rohini -> Rishabham
        assertEquals(8, LunarCalendarUtils.getBirthRasi("STAR_17")) // Anuradha -> Vrischigam
        
        // Boundary stars
        assertEquals(1, LunarCalendarUtils.getBirthRasi("STAR_3_1")) // Krittika P1 -> Mesham
        assertEquals(2, LunarCalendarUtils.getBirthRasi("STAR_3_2")) // Krittika P2-4 -> Rishabham
        assertEquals(9, LunarCalendarUtils.getBirthRasi("STAR_21_1")) // Uttarashada P1 -> Dhanusu
        assertEquals(10, LunarCalendarUtils.getBirthRasi("STAR_21_2")) // Uttarashada P2-4 -> Makaram
    }

    @Test
    fun test8thRasiCalculation() {
        fun calculate8th(birthRasi: Int): Int {
            return ((birthRasi - 1 + 7) % 12) + 1
        }
        
        assertEquals(8, calculate8th(1))  // Mesham -> Vrischigam
        assertEquals(2, calculate8th(7))  // Thulam -> Rishabham
        assertEquals(9, calculate8th(2))  // Rishabham -> Dhanusu
        assertEquals(12, calculate8th(5)) // Simmam -> Meenam
        assertEquals(5, calculate8th(10)) // Makaram -> Simmam
    }

    @Test
    fun testMoonRasiDetection() {
        // We can't easily mock the high-precision longitudes without a lot of setup,
        // but we can check if the function returns values in the 1-12 range.
        val now = Instant.now()
        val rasi = LunarCalendarUtils.getMoonRasi(now)
        assertTrue("Rasi should be between 1 and 12", rasi in 1..12)
    }

    private fun assertTrue(message: String, condition: Boolean) {
        org.junit.Assert.assertTrue(message, condition)
    }
}
