package com.suresh.sacredtimeline.model

import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class DayData(
    val nallaNeram: List<NallaNeram>,
    val gowriNeram: List<GowriNeram>,
    val hora: List<Hora>,
    val specialPeriods: List<SpecialPeriod>,
    @Serializable(with = LocalTimeSerializer::class)
    val sunrise: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    val sunset: LocalTime,
    val isFallback: Boolean,
    val tamilDay: Int = 0,
    val tamilMonthResId: Int = 0,
    val tamilYearResId: Int = 0
)
