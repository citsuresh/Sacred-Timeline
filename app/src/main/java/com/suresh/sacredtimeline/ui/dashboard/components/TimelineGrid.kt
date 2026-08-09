package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
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
private val TIME_COLUMN_WIDTH = 65.dp

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
    val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm")
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
                
                val timeString = time.format(timeFormatter)
                
                Column(
                    modifier = Modifier
                        .offset(y = topOffset - 12.dp)
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNight) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.End
                    )
                    if (!is24Hour) {
                        val amPmLabel = if (time.hour < 12) {
                            stringResource(com.suresh.sacredtimeline.R.string.label_am)
                        } else {
                            stringResource(com.suresh.sacredtimeline.R.string.label_pm)
                        }
                        Text(
                            text = amPmLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = (if (isNight) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 9.sp,
                            textAlign = TextAlign.End,
                            lineHeight = 10.sp
                        )
                    }
                }
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
