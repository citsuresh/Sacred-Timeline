package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.R
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TamilCalendarUtilsTest {

    @Test
    fun testAadiKiruthigaiDetection() {
        // Aadi Month, 18th day (just an example day in Aadi)
        val tamilDate = TamilCalendarUtils.TamilDate(
            day = 10,
            monthResId = R.string.month_aadi,
            yearResId = R.string.year_1
        )
        
        // Nakshatra 3 is Krittika
        val lunarInfo = LunarCalendarUtils.LunarInfo(
            tithi = 1,
            nakshatra = 3,
            pakshaResId = 0,
            pakshaDay = 1,
            tithiResId = 0,
            nakshatraResId = 0
        )
        
        val events = TamilCalendarUtils.getSpecialEvents(tamilDate, lunarInfo)
        
        assertTrue("Aadi Kiruthigai should be detected", events.contains(R.string.event_aadi_kiruthigai))
    }

    @Test
    fun testAadiKiruthigaiNegativeCase() {
        // Aani Month, Nakshatra 3
        val tamilDate = TamilCalendarUtils.TamilDate(
            day = 10,
            monthResId = R.string.month_aani,
            yearResId = R.string.year_1
        )
        
        val lunarInfo = LunarCalendarUtils.LunarInfo(
            tithi = 1,
            nakshatra = 3,
            pakshaResId = 0,
            pakshaDay = 1,
            tithiResId = 0,
            nakshatraResId = 0
        )
        
        val events = TamilCalendarUtils.getSpecialEvents(tamilDate, lunarInfo)
        
        assertTrue("Aadi Kiruthigai should NOT be detected in Aani month", !events.contains(R.string.event_aadi_kiruthigai))
    }
}
