package com.suresh.sacredtimeline.model

import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class SubHora(
    override val name: String,
    override val tamilName: String,
    @Serializable(with = LocalTimeSerializer::class)
    override val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    override val endTime: LocalTime,
    override val auspiciousness: Auspiciousness,
    override val description: String = "",
    val planet: String,
    val compatibility: HoraCompatibility = HoraCompatibility.NEUTRAL
) : Timing
