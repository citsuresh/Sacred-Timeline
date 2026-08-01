package com.suresh.sacredtimeline.model

import java.time.LocalTime

enum class HoraCompatibility {
    FAVORABLE, CONFLICTING, NEUTRAL
}

sealed interface Timing {
    val name: String
    val startTime: LocalTime
    val endTime: LocalTime
    val auspiciousness: Auspiciousness
    val tamilName: String
    val description: String

    fun isCurrent(time: LocalTime): Boolean {
        return !time.isBefore(startTime) && time.isBefore(endTime)
    }
}

data class NallaNeram(
    override val name: String,
    override val tamilName: String,
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = ""
) : Timing

data class GowriNeram(
    override val name: String,
    override val tamilName: String,
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = ""
) : Timing

data class Hora(
    override val name: String,
    override val tamilName: String,
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = "",
    val compatibility: HoraCompatibility = HoraCompatibility.NEUTRAL
) : Timing

data class SpecialPeriod(
    override val name: String,
    override val tamilName: String,
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = ""
) : Timing
