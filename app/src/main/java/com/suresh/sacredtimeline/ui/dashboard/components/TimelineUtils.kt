package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.ui.unit.Dp
import java.time.LocalTime

private const val START_HOUR = 0

fun calculateOffset(time: LocalTime, hourHeight: Dp): Dp {
    val fraction = (time.hour - START_HOUR) + time.minute / 60f + time.second / 3600f
    return hourHeight * fraction
}
