package com.suresh.sacredtimeline.model

import java.time.Instant
import java.time.LocalTime

sealed interface DashboardDetail {
    val title: Int
    val description: Int
    
    data class TimelineTiming(
        val timing: Timing
    ) : DashboardDetail {
        override val title: Int = 0 // Resolved dynamically via Metadata
        override val description: Int = 0 // Resolved dynamically via Metadata
    }

    data class Lunar(
        val item: LunarInterval,
        val type: LunarType
    ) : DashboardDetail {
        override val title: Int = item.resId
        override val description: Int = 0 // Resolved dynamically via Metadata
    }

    enum class LunarType { TITHI, NAKSHATRA, PAKSHA }

    data class SpecialEvent(
        val resId: Int,
        val startTime: LocalTime? = null,
        val endTime: LocalTime? = null
    ) : DashboardDetail {
        override val title: Int = resId
        override val description: Int = 0 // Resolved dynamically via Metadata
    }

    data class Muhurtham(
        val nameResId: Int,
        val startTime: LocalTime,
        val endTime: LocalTime
    ) : DashboardDetail {
        override val title: Int = nameResId
        override val description: Int = 0 // Resolved dynamically via Metadata
    }
}
