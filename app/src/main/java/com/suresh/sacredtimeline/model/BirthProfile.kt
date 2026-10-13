package com.suresh.sacredtimeline.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class ChartStyle {
    SOUTH_INDIAN,
    NORTH_INDIAN
}

@Serializable
enum class LunarMonthSystem {
    AMANTA,
    PURNIMANTA
}

@Serializable
data class BirthProfile(
    val id: String = UUID.randomUUID().toString(),
    val nameEnglish: String,
    val nameRegional: String = "",
    val birthDate: String, // YYYY-MM-DD
    val birthTime: String, // HH:mm
    val birthPlace: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double = 5.5,
    val chartStyle: ChartStyle = ChartStyle.SOUTH_INDIAN,
    val lunarSystem: LunarMonthSystem = LunarMonthSystem.AMANTA,
    val isPrimary: Boolean = false
)
