package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.R
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class TamilCalendarUtilsTest {

    private val defaultSunrise = Instant.parse("2026-08-10T06:10:00Z")
    private val defaultPradosha = LunarCalendarUtils.RitualWindow(
        Instant.parse("2026-08-10T17:13:00Z"),
        Instant.parse("2026-08-10T18:43:00Z")
    )
    private val defaultNishita = LunarCalendarUtils.RitualWindow(
        Instant.parse("2026-08-10T23:40:00Z"),
        Instant.parse("2026-08-11T00:20:00Z")
    )

    @Test
    fun testPradoshamPositiveCase() {
        val tamilDate = TamilCalendarUtils.TamilDate(10, R.string.month_aadi, R.string.year_1)
        
        // Tithi 13 exists during Pradosha window
        val tithis = listOf(
            LunarCalendarUtils.LunarInterval(13, 0, 
                defaultPradosha.startTime.minusSeconds(3600), 
                defaultPradosha.endTime.plusSeconds(3600)
            )
        )
        
        val context = TamilCalendarUtils.RitualContext(tithis, emptyList(), defaultSunrise, defaultPradosha, defaultNishita)
        val events = TamilCalendarUtils.getSpecialEvents(tamilDate, context)
        
        assertTrue("Pradosham should be detected when Tithi 13 covers the window", events.contains(R.string.event_pradosham))
    }

    @Test
    fun testPradoshamNegativeCase_WrongTithi() {
        val tamilDate = TamilCalendarUtils.TamilDate(10, R.string.month_aadi, R.string.year_1)
        
        val tithis = listOf(
            LunarCalendarUtils.LunarInterval(12, 0, defaultPradosha.startTime, defaultPradosha.endTime)
        )
        
        val context = TamilCalendarUtils.RitualContext(tithis, emptyList(), defaultSunrise, defaultPradosha, defaultNishita)
        val events = TamilCalendarUtils.getSpecialEvents(tamilDate, context)
        
        assertFalse("Pradosham should NOT be detected for Tithi 12", events.contains(R.string.event_pradosham))
    }

    @Test
    fun testPradoshamNegativeCase_WrongTime() {
        val tamilDate = TamilCalendarUtils.TamilDate(10, R.string.month_aadi, R.string.year_1)
        
        // Tithi 13 exists but ENDS before Pradosha window starts
        val tithis = listOf(
            LunarCalendarUtils.LunarInterval(13, 0, 
                defaultPradosha.startTime.minusSeconds(7200), 
                defaultPradosha.startTime.minusSeconds(100)
            )
        )
        
        val context = TamilCalendarUtils.RitualContext(tithis, emptyList(), defaultSunrise, defaultPradosha, defaultNishita)
        val events = TamilCalendarUtils.getSpecialEvents(tamilDate, context)
        
        assertFalse("Pradosham should NOT be detected if Tithi 13 ends before the window", events.contains(R.string.event_pradosham))
    }

    @Test
    fun testShivaratriDetection() {
        val tamilDate = TamilCalendarUtils.TamilDate(11, R.string.month_aadi, R.string.year_1)
        
        val tithis = listOf(
            LunarCalendarUtils.LunarInterval(29, 0, defaultNishita.startTime, defaultNishita.endTime)
        )
        
        val context = TamilCalendarUtils.RitualContext(tithis, emptyList(), defaultSunrise, defaultPradosha, defaultNishita)
        val events = TamilCalendarUtils.getSpecialEvents(tamilDate, context)
        
        assertTrue("Shivaratri should be detected during Nishita Kala", events.contains(R.string.event_sivaratri))
    }

    @Test
    fun testUdayaVyapini_AadiPooram() {
        val tamilDate = TamilCalendarUtils.TamilDate(10, R.string.month_aadi, R.string.year_1)
        
        // Nakshatra 11 (Pooram) is present at Sunrise
        val nakshatras = listOf(
            LunarCalendarUtils.LunarInterval(11, 0, defaultSunrise.minusSeconds(1000), defaultSunrise.plusSeconds(1000))
        )
        
        val context = TamilCalendarUtils.RitualContext(emptyList(), nakshatras, defaultSunrise, defaultPradosha, defaultNishita)
        val events = TamilCalendarUtils.getSpecialEvents(tamilDate, context)
        
        assertTrue("Aadi Pooram should be detected if star 11 is at Sunrise", events.contains(R.string.event_aadi_pooram))
    }

    @Test
    fun testUdayaVyapini_AadiPooramNegative() {
        val tamilDate = TamilCalendarUtils.TamilDate(10, R.string.month_aadi, R.string.year_1)
        
        // Nakshatra 11 exists during the day but NOT at Sunrise
        val nakshatras = listOf(
            LunarCalendarUtils.LunarInterval(11, 0, defaultSunrise.plusSeconds(1000), defaultSunrise.plusSeconds(2000))
        )
        
        val context = TamilCalendarUtils.RitualContext(emptyList(), nakshatras, defaultSunrise, defaultPradosha, defaultNishita)
        val events = TamilCalendarUtils.getSpecialEvents(tamilDate, context)
        
        assertFalse("Aadi Pooram should NOT be detected if star 11 starts after Sunrise", events.contains(R.string.event_aadi_pooram))
    }
}
