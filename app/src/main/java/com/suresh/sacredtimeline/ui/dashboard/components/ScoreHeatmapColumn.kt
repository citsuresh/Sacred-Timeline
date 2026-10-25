package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.logic.ScoringEngine
import com.suresh.sacredtimeline.model.BirthProfile
import com.suresh.sacredtimeline.model.DayData
import com.suresh.sacredtimeline.ui.theme.SacredTimelineColors
import java.time.LocalTime

@Composable
fun ScoreHeatmapColumn(
    dayData: DayData,
    activeProfile: BirthProfile?,
    hourHeight: Dp,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val intervals = remember(dayData, activeProfile) {
        val times = mutableSetOf(LocalTime.MIN, LocalTime.MAX)
        val allTimings = dayData.nallaNeram +
                dayData.gowriNeram +
                dayData.hora +
                dayData.specialPeriods +
                dayData.chandrashtamam +
                dayData.yogam +
                dayData.tharaBalam

        allTimings.forEach { timing ->
            times.add(timing.startTime)
            times.add(timing.endTime)
        }
        dayData.brahmaMuhurtham?.let { times.add(it.startTime); times.add(it.endTime) }
        dayData.abhijitMuhurtham?.let { times.add(it.startTime); times.add(it.endTime) }

        val sortedTimes = times.sorted()
        val list = mutableListOf<HeatmapInterval>()

        for (i in 0 until sortedTimes.size - 1) {
            val start = sortedTimes[i]
            val end = sortedTimes[i + 1]
            if (start == end) continue

            val midMinutes = (start.toSecondOfDay() + end.toSecondOfDay()) / 2
            val midTime = LocalTime.ofSecondOfDay(midMinutes.toLong().coerceIn(0, 86399))
            
            val result = ScoringEngine.calculateScore(dayData, activeProfile, midTime)
            val color = scoreToColor(result.totalScore)

            list.add(HeatmapInterval(start, end, result.totalScore, color))
        }
        list
    }

    Box(
        modifier = modifier
            .width(32.dp)
            .height(hourHeight * 24)
            .background(Color.Transparent)
    ) {
        intervals.forEach { interval ->
            val topOffset = calculateOffset(interval.startTime, hourHeight)
            val bottomOffset = calculateOffset(interval.endTime, hourHeight)
            val height = (bottomOffset - topOffset).coerceAtLeast(16.dp)

            val textColor = SacredTimelineColors.getContentColor(interval.color)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .offset(y = topOffset)
                    .background(interval.color, RoundedCornerShape(4.dp))
                    .padding(top = 4.dp, start = 2.dp, end = 2.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                val cardTopPx = with(density) { topOffset.toPx() }
                val cardBottomPx = with(density) { bottomOffset.toPx() }
                val cardHeightPx = cardBottomPx - cardTopPx
                val textHeightPx = with(density) { 16.dp.toPx() }
                val maxScrollOffset = (cardHeightPx - textHeightPx).coerceAtLeast(0f)

                Text(
                    text = "${interval.score.toInt()}%",
                    color = textColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    modifier = Modifier
                        .offset {
                            val currentScroll = scrollState.value.toFloat()
                            val screenY = cardTopPx - currentScroll
                            val dy = when {
                                screenY >= 0f -> 0f
                                else -> (-screenY).coerceIn(0f, maxScrollOffset)
                            }
                            IntOffset(0, dy.toInt())
                        }
                )
            }
        }
    }
}

data class HeatmapInterval(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val score: Float,
    val color: Color
)

private fun scoreToColor(score: Float): Color {
    return when {
        score >= 80f -> Color(0xFF4CAF50) // Deep Green
        score >= 65f -> Color(0xFF8BC34A) // Light Green
        score >= 50f -> Color(0xFFFFEB3B) // Yellow
        score >= 35f -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFE53935)         // Red
    }
}
