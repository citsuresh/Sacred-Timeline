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
