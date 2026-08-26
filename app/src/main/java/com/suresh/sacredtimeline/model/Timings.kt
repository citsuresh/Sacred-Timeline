package com.suresh.sacredtimeline.model

import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
enum class HoraCompatibility {
    FAVORABLE, CONFLICTING, NEUTRAL
}

@Serializable
sealed interface Timing {
    val name: String
    @Serializable(with = LocalTimeSerializer::class)
    val startTime: LocalTime
    @Serializable(with = LocalTimeSerializer::class)
    val endTime: LocalTime
    val auspiciousness: Auspiciousness
    val tamilName: String
    val description: String

    fun isCurrent(time: LocalTime): Boolean {
        return !time.isBefore(startTime) && time.isBefore(endTime)
    }
}

@Serializable
data class NallaNeram(
    override val name: String,
    override val tamilName: String,
    @Serializable(with = LocalTimeSerializer::class)
    override val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = ""
) : Timing

@Serializable
data class GowriNeram(
    override val name: String,
    override val tamilName: String,
    @Serializable(with = LocalTimeSerializer::class)
    override val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = ""
) : Timing

@Serializable
data class Hora(
    override val name: String,
    override val tamilName: String,
    @Serializable(with = LocalTimeSerializer::class)
    override val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = "",
    val compatibility: HoraCompatibility = HoraCompatibility.NEUTRAL
) : Timing

@Serializable
data class SpecialPeriod(
    override val name: String,
    override val tamilName: String,
    @Serializable(with = LocalTimeSerializer::class)
    override val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = ""
) : Timing

@Serializable
data class Muhurtham(
    override val name: String,
    override val tamilName: String,
    @Serializable(with = LocalTimeSerializer::class)
    override val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = ""
) : Timing

@Serializable
data class MaitraMuhurtham(
    override val name: String,
    override val tamilName: String,
    @Serializable(with = LocalTimeSerializer::class)
    override val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = "",
    val potencyStars: Int
) : Timing

@Serializable
data class ChandrashtamamTiming(
    override val name: String,
    override val tamilName: String,
    @Serializable(with = LocalTimeSerializer::class)
    override val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness = Auspiciousness.RED,
    override val description: String = "",
    val starResId: Int = 0,
    val birthRasiResId: Int = 0,
    val transitRasiResId: Int = 0,
    @Serializable(with = InstantSerializer::class)
    val startTimeInstant: java.time.Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val endTimeInstant: java.time.Instant? = null
) : Timing

@Serializable
data class YogamTiming(
    override val name: String, // Amirtha, Siddha, Marana, Vadha
    override val tamilName: String,
    @Serializable(with = LocalTimeSerializer::class)
    override val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = "",
    val categoryResId: Int = 0,
    val sanskritNameResId: Int = 0
) : Timing

@Serializable
data class TharaBalamTiming(
    override val name: String, // 1 to 9
    override val tamilName: String,
    @Serializable(with = LocalTimeSerializer::class)
    override val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = "",
    val categoryIndex: Int, // 1 to 9
    val categoryResId: Int = 0,
    val sanskritNameResId: Int = 0
) : Timing
