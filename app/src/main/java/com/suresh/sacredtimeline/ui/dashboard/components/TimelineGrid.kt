package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.ui.theme.SeparatorGrey
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val START_HOUR = 0
private const val END_HOUR = 24
private val TIME_COLUMN_WIDTH = 60.dp

@Composable
fun TimeGrid(hourHeight: Dp) {
    val showHalfHourLine = hourHeight >= 50.dp
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 1.dp.toPx()
        for (hour in START_HOUR..END_HOUR) {
            val yHour = (hour - START_HOUR) * hourHeight.toPx()
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(0f, yHour),
                end = Offset(size.width, yHour),
                strokeWidth = strokeWidth
            )
            
            if (hour < END_HOUR && showHalfHourLine) {
                val yHalf = yHour + (hourHeight.toPx() / 2)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    start = Offset(0f, yHalf),
                    end = Offset(size.width, yHalf),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

@Composable
fun TimeMarkersColumn(sunrise: LocalTime, sunset: LocalTime, hourHeight: Dp, is24Hour: Boolean) {
    val pattern = if (is24Hour) "HH:mm" else "h:mm a"
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val showHalfHour = hourHeight >= 60.dp

    Box(
        modifier = Modifier
            .width(TIME_COLUMN_WIDTH)
            .fillMaxHeight()
    ) {
        for (hour in START_HOUR until END_HOUR) {
            val minutes = if (showHalfHour) listOf(0, 30) else listOf(0)
            for (minute in minutes) {
                val time = LocalTime.of(hour, minute)
                val topOffset = calculateOffset(time, hourHeight)
                val isNight = time.isBefore(sunrise) || !time.isBefore(sunset)
                
                Text(
                    text = time.format(formatter),
                    modifier = Modifier
                        .offset(y = topOffset - 8.dp)
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isNight) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun NightShade(sunrise: LocalTime, sunset: LocalTime, hourHeight: Dp) {
    val sunsetOffset = calculateOffset(sunset, hourHeight)
    val sunriseOffset = calculateOffset(sunrise, hourHeight)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(sunriseOffset)
            .background(Color.Black.copy(alpha = 0.15f))
    )
    
    val dayEndOffset = calculateOffset(LocalTime.MAX, hourHeight)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = sunsetOffset)
            .height(dayEndOffset - sunsetOffset)
            .background(Color.Black.copy(alpha = 0.15f))
    )
}
