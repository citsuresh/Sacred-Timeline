package com.suresh.sacredtimeline.model

import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class LunarInterval(
    val value: Int,
    val resId: Int,
    @Serializable(with = InstantSerializer::class)
    val startTime: java.time.Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val endTime: java.time.Instant? = null
)

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
    val tamilYearResId: Int = 0,
    val pakshaResId: Int = 0,
    val pakshaDay: Int = 0,
    val tithis: List<LunarInterval> = emptyList(),
    val nakshatras: List<LunarInterval> = emptyList(),
    val specialEvents: List<Int> = emptyList(),
    val isSubhaMuhurtham: Boolean = false,
    val brahmaMuhurtham: Muhurtham? = null,
    val abhijitMuhurtham: Muhurtham? = null,
    val maitraMuhurtham: List<MaitraMuhurtham> = emptyList()
)
