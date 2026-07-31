package com.suresh.sacredtimeline.model

import java.time.LocalTime

sealed interface Timing {
    val startTime: LocalTime
    val endTime: LocalTime
    val auspiciousness: Auspiciousness

    fun isCurrent(time: LocalTime): Boolean {
        return !time.isBefore(startTime) && time.isBefore(endTime)
    }
}

data class NallaNeram(
    val name: String,
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness
) : Timing

data class GowriNeram(
    val name: String,
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness
) : Timing

data class Hora(
    val name: String,
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness
) : Timing

data class SpecialPeriod(
    val name: String,
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness
) : Timing
