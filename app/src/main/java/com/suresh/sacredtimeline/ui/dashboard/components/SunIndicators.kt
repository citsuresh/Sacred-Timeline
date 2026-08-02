package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SunTimesDisplay(sunrise: LocalTime, sunset: LocalTime, isFallback: Boolean, isLandscape: Boolean, is24Hour: Boolean) {
    val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm")
    val amLabel = stringResource(R.string.label_am)
    val pmLabel = stringResource(R.string.label_pm)
    
    fun formatWithAmPm(time: LocalTime): String {
        if (is24Hour) return time.format(timeFormatter)
        val period = if (time.hour < 12) amLabel else pmLabel
        return "${time.format(timeFormatter)} $period"
    }

    if (isLandscape) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF9800))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${stringResource(R.string.label_sunrise)}: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(formatWithAmPm(sunrise), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WbTwilight, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF5722))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${stringResource(R.string.label_sunset)}:  ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(formatWithAmPm(sunset), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                if (isFallback) {
                    Text(stringResource(R.string.label_approximate), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), fontSize = 9.sp)
                }
            }
        }
    } else {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.WbSunny, 
                    contentDescription = null, 
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFF9800)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${stringResource(R.string.label_sunrise)}: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(formatWithAmPm(sunrise), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.WbTwilight, 
                    contentDescription = null, 
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFF5722)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${stringResource(R.string.label_sunset)}: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(formatWithAmPm(sunset), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            
            if (isFallback) {
                Text(
                    stringResource(R.string.label_approximate), 
                    style = MaterialTheme.typography.labelSmall, 
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), 
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun SunGridMarker(time: LocalTime, label: String, icon: ImageVector, iconTint: Color, hourHeight: Dp, is24Hour: Boolean) {
    val topOffset = calculateOffset(time, hourHeight)
    val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm")
    
    val displayTime = if (is24Hour) {
        time.format(timeFormatter)
    } else {
        val amPm = if (time.hour < 12) stringResource(R.string.label_am) else stringResource(R.string.label_pm)
        "${time.format(timeFormatter)} $amPm"
    }

    val sectionLabel = if (label == "Sunrise") stringResource(R.string.label_day_muhurat) else stringResource(R.string.label_night_muhurat)
    
    val timeColumnWidth = 65.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = topOffset - 18.dp)
            .padding(start = 4.dp, end = 4.dp)
            .heightIn(min = 36.dp)
    ) {
        // Full width separator line with glow
        Canvas(modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.Center)) {
            drawRect(
                color = iconTint.copy(alpha = 0.3f),
                topLeft = Offset(0f, -4f),
                size = androidx.compose.ui.geometry.Size(size.width, 10f)
            )
            drawLine(
                color = iconTint,
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = 4f
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = timeColumnWidth + 4.dp, end = 4.dp)
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Muhurat Label
            Surface(
                color = Color.Black.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, iconTint.copy(alpha = 0.8f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (label == "Sunrise") "🌅" else "🌇", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = sectionLabel,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }

            // Time Label
            Surface(
                color = Color.Black.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, iconTint.copy(alpha = 0.8f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = iconTint)
                    Spacer(modifier = Modifier.width(4.dp))
                    val localizedLabelFull = if (label == "Sunrise") stringResource(R.string.label_sunrise) else stringResource(R.string.label_sunset)
                    Text(
                        text = "$localizedLabelFull: $displayTime",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun BoxScope.NowIndicator(currentTime: LocalTime, hourHeight: Dp, color: Color = Color.Red, thickness: Dp = 2.dp) {
    val topOffset = calculateOffset(currentTime, hourHeight)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = topOffset - (thickness / 2))
            .height(thickness)
            .background(color)
    )
    
    Surface(
        color = color,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .offset(y = topOffset - 10.dp)
            .padding(start = 8.dp)
            .align(Alignment.TopStart)
    ) {
        Text(
            text = stringResource(R.string.label_now_line),
            color = if (color == Color.White) Color.Black else Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}
