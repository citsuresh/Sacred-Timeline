package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.R
import java.time.LocalDate

object TamilCalendarUtils {

    data class TamilDate(
        val day: Int,
        val monthResId: Int,
        val yearResId: Int
    )

    fun getTamilDate(date: LocalDate): TamilDate {
        val month = date.monthValue
        val day = date.dayOfMonth
        val year = date.year

        val monthResId: Int
        val startDay: Int
        val startMonth: Int
        val startYear: Int

        when (month) {
            1 -> if (day < 14) {
                monthResId = R.string.month_margazhi
                startDay = 16; startMonth = 12; startYear = year - 1
            } else {
                monthResId = R.string.month_thai
                startDay = 14; startMonth = 1; startYear = year
            }
            2 -> if (day < 13) {
                monthResId = R.string.month_thai
                startDay = 14; startMonth = 1; startYear = year
            } else {
                monthResId = R.string.month_maasi
                startDay = 13; startMonth = 2; startYear = year
            }
            3 -> if (day < 14) {
                monthResId = R.string.month_maasi
                startDay = 13; startMonth = 2; startYear = year
            } else {
                monthResId = R.string.month_panguni
                startDay = 14; startMonth = 3; startYear = year
            }
            4 -> if (day < 14) {
                monthResId = R.string.month_panguni
                startDay = 14; startMonth = 3; startYear = year
            } else {
                monthResId = R.string.month_chithirai
                startDay = 14; startMonth = 4; startYear = year
            }
            5 -> if (day < 15) {
                monthResId = R.string.month_chithirai
                startDay = 14; startMonth = 4; startYear = year
            } else {
                monthResId = R.string.month_vaikasi
                startDay = 15; startMonth = 5; startYear = year
            }
            6 -> if (day < 15) {
                monthResId = R.string.month_vaikasi
                startDay = 15; startMonth = 5; startYear = year
            } else {
                monthResId = R.string.month_aani
                startDay = 15; startMonth = 6; startYear = year
            }
            7 -> if (day < 17) {
                monthResId = R.string.month_aani
                startDay = 15; startMonth = 6; startYear = year
            } else {
                monthResId = R.string.month_aadi
                startDay = 17; startMonth = 7; startYear = year
            }
            8 -> if (day < 17) {
                monthResId = R.string.month_aadi
                startDay = 17; startMonth = 7; startYear = year
            } else {
                monthResId = R.string.month_avani
                startDay = 17; startMonth = 8; startYear = year
            }
            9 -> if (day < 17) {
                monthResId = R.string.month_avani
                startDay = 17; startMonth = 8; startYear = year
            } else {
                monthResId = R.string.month_purattasi
                startDay = 17; startMonth = 9; startYear = year
            }
            10 -> if (day < 18) {
                monthResId = R.string.month_purattasi
                startDay = 17; startMonth = 9; startYear = year
            } else {
                monthResId = R.string.month_aippasi
                startDay = 18; startMonth = 10; startYear = year
            }
            11 -> if (day < 17) {
                monthResId = R.string.month_aippasi
                startDay = 18; startMonth = 10; startYear = year
            } else {
                monthResId = R.string.month_karthigai
                startDay = 17; startMonth = 11; startYear = year
            }
            12 -> if (day < 16) {
                monthResId = R.string.month_karthigai
                startDay = 17; startMonth = 11; startYear = year
            } else {
                monthResId = R.string.month_margazhi
                startDay = 16; startMonth = 12; startYear = year
            }
            else -> {
                monthResId = R.string.month_chithirai
                startDay = 14; startMonth = 4; startYear = year
            }
        }

        val startDate = LocalDate.of(startYear, startMonth, startDay)
        val tamilDay = java.time.temporal.ChronoUnit.DAYS.between(startDate, date).toInt() + 1
        val yearResId = getTamilYearResId(date)

        return TamilDate(tamilDay, monthResId, yearResId)
    }

    data class RitualContext(
        val tithis: List<LunarCalendarUtils.LunarInterval>,
        val nakshatras: List<LunarCalendarUtils.LunarInterval>,
        val sunrise: java.time.Instant,
        val pradosham: LunarCalendarUtils.RitualWindow,
        val nishita: LunarCalendarUtils.RitualWindow,
        val zoneId: java.time.ZoneId = java.time.ZoneId.systemDefault()
    )

    /**
     * Identifies special events/festivals based on Solar and Lunar combinations.
     * Uses traditional anchoring (Sunrise, Sunset, Nishita Kala) to prevent double-counting.
     */
    fun getSpecialEvents(
        tamilDate: TamilDate,
        context: RitualContext
    ): List<Int> {
        val events = mutableListOf<Int>()

        // 1. Solar Fixed Festivals (Day-based)
        if (tamilDate.monthResId == R.string.month_aadi && tamilDate.day == 18) {
            events.add(R.string.event_aadi_perukku)
        }
        if (tamilDate.monthResId == R.string.month_thai && tamilDate.day == 1) {
            events.add(R.string.event_pongal)
        }
        if (tamilDate.monthResId == R.string.month_chithirai && tamilDate.day == 1) {
            events.add(R.string.event_tamil_new_year)
        }

        // 2. Window-Based Festivals
        // Pradosham: Trayodashi (13 or 28) during Pradosha window [Sunset - 90m, Sunset]
        if (context.tithis.any { (it.value == 13 || it.value == 28) && context.pradosham.overlaps(it) }) {
            events.add(R.string.event_pradosham)
        }

        // Shivaratri: Krishna Chaturdashi (29) during Nishita Kala (Midnight window)
        if (context.tithis.any { it.value == 29 && context.nishita.overlaps(it) }) {
            events.add(R.string.event_sivaratri)
        }

        // 3. Sunrise-Based Festivals (Udaya Vyapini)
        val tithiAtSunrise = context.tithis.find { interval ->
            val start = interval.startTime ?: java.time.Instant.MIN
            val end = interval.endTime ?: java.time.Instant.MAX
            !context.sunrise.isBefore(start) && context.sunrise.isBefore(end)
        }?.value
        
        val nakshatraAtSunrise = context.nakshatras.find { interval ->
            val start = interval.startTime ?: java.time.Instant.MIN
            val end = interval.endTime ?: java.time.Instant.MAX
            !context.sunrise.isBefore(start) && context.sunrise.isBefore(end)
        }?.value

        // Aadi Pooram: Aadi Month + Pooram Star (11)
        if (tamilDate.monthResId == R.string.month_aadi && nakshatraAtSunrise == 11) {
            events.add(R.string.event_aadi_pooram)
        }
        
        // Aadi Kiruthigai: Aadi Month + Krittika Star (3)
        if (tamilDate.monthResId == R.string.month_aadi && nakshatraAtSunrise == 3) {
            events.add(R.string.event_aadi_kiruthigai)
        }
        
        // Naga Chaturthi: Sawan/Aavani Shukla Chaturthi (4)
        if (tamilDate.monthResId == R.string.month_avani && tithiAtSunrise == 4) {
            events.add(R.string.event_naga_chaturthi)
        }

        return events
    }

    /**
     * Returns the resource ID for the Tamil Year name in the 60-year cycle.
     */
    fun getTamilYearResId(date: LocalDate): Int {
        var year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth

        // Tamil New Year starts on April 14 (approx)
        if (month < 4 || (month == 4 && day < 14)) {
            year -= 1
        }

        val yearIndex = (year + 54) % 60
        val finalIndex = if (yearIndex == 0) 60 else yearIndex

        return when (finalIndex) {
            1 -> R.string.year_1
            2 -> R.string.year_2
            3 -> R.string.year_3
            4 -> R.string.year_4
            5 -> R.string.year_5
            6 -> R.string.year_6
            7 -> R.string.year_7
            8 -> R.string.year_8
            9 -> R.string.year_9
            10 -> R.string.year_10
            11 -> R.string.year_11
            12 -> R.string.year_12
            13 -> R.string.year_13
            14 -> R.string.year_14
            15 -> R.string.year_15
            16 -> R.string.year_16
            17 -> R.string.year_17
            18 -> R.string.year_18
            19 -> R.string.year_19
            20 -> R.string.year_20
            21 -> R.string.year_21
            22 -> R.string.year_22
            23 -> R.string.year_23
            24 -> R.string.year_24
            25 -> R.string.year_25
            26 -> R.string.year_26
            27 -> R.string.year_27
            28 -> R.string.year_28
            29 -> R.string.year_29
            30 -> R.string.year_30
            31 -> R.string.year_31
            32 -> R.string.year_32
            33 -> R.string.year_33
            34 -> R.string.year_34
            35 -> R.string.year_35
            36 -> R.string.year_36
            37 -> R.string.year_37
            38 -> R.string.year_38
            39 -> R.string.year_39
            40 -> R.string.year_40
            41 -> R.string.year_41
            42 -> R.string.year_42
            43 -> R.string.year_43
            44 -> R.string.year_44
            45 -> R.string.year_45
            46 -> R.string.year_46
            47 -> R.string.year_47
            48 -> R.string.year_48
            49 -> R.string.year_49
            50 -> R.string.year_50
            51 -> R.string.year_51
            52 -> R.string.year_52
            53 -> R.string.year_53
            54 -> R.string.year_54
            55 -> R.string.year_55
            56 -> R.string.year_56
            57 -> R.string.year_57
            58 -> R.string.year_58
            59 -> R.string.year_59
            60 -> R.string.year_60
            else -> R.string.year_1 // Fallback
        }
    }
}
