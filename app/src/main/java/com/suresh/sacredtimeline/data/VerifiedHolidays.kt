package com.suresh.sacredtimeline.data

import com.suresh.sacredtimeline.R
import java.time.LocalDate

object VerifiedHolidays {

    /**
     * Map of Date to List of String Resource IDs for Public Holidays.
     */
    private val HOLIDAYS = mapOf(
        // 2026
        LocalDate.of(2026, 1, 14) to listOf(R.string.event_pongal),
        LocalDate.of(2026, 1, 15) to listOf(R.string.event_thiruvalluvar_day),
        LocalDate.of(2026, 1, 26) to listOf(R.string.event_republic_day),
        LocalDate.of(2026, 4, 14) to listOf(R.string.event_tamil_new_year),
        LocalDate.of(2026, 8, 15) to listOf(R.string.event_independence_day),
        LocalDate.of(2026, 8, 25) to listOf(R.string.event_milad_un_nabi),
        LocalDate.of(2026, 10, 2) to listOf(R.string.event_gandhi_jayanthi),
        LocalDate.of(2026, 11, 8) to listOf(R.string.event_deepavali),
        LocalDate.of(2026, 12, 25) to listOf(R.string.event_christmas)
    )

    fun getHolidays(date: LocalDate): List<Int> {
        return HOLIDAYS[date] ?: emptyList()
    }
    
    /**
     * Verified Subha Muhurtham dates for 2026 based on Tamil Panchangam.
     */
    private val SUBHA_MUHURTHAMS = setOf(
        LocalDate.of(2026, 1, 28), LocalDate.of(2026, 1, 29), LocalDate.of(2026, 1, 30), LocalDate.of(2026, 1, 31),
        LocalDate.of(2026, 2, 20), LocalDate.of(2026, 2, 22),
        LocalDate.of(2026, 4, 19), LocalDate.of(2026, 4, 20), LocalDate.of(2026, 4, 23), LocalDate.of(2026, 4, 30),
        LocalDate.of(2026, 5, 18), LocalDate.of(2026, 5, 28), LocalDate.of(2026, 5, 29),
        LocalDate.of(2026, 6, 17), LocalDate.of(2026, 6, 18), LocalDate.of(2026, 6, 24), LocalDate.of(2026, 6, 25),
        LocalDate.of(2026, 8, 23), LocalDate.of(2026, 8, 28), LocalDate.of(2026, 8, 30), LocalDate.of(2026, 8, 31),
        LocalDate.of(2026, 11, 11), LocalDate.of(2026, 11, 13), LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 16), LocalDate.of(2026, 11, 20)
    )

    fun isSubhaMuhurtham(date: LocalDate): Boolean {
        return SUBHA_MUHURTHAMS.contains(date)
    }
}
