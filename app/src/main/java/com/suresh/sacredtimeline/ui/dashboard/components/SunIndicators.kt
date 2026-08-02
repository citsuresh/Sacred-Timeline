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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SunTimesDisplay(sunrise: LocalTime, sunset: LocalTime, isFallback: Boolean, isLandscape: Boolean, is24Hour: Boolean) {
    val pattern = if (is24Hour) "HH:mm" else "hh:mm a"
    val formatter = DateTimeFormatter.ofPattern(pattern)
    
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
                    Text("Sunrise: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(sunrise.format(formatter), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WbTwilight, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF5722))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sunset:  ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(sunset.format(formatter), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                if (isFallback) {
                    Text("(approximate)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), fontSize = 9.sp)
                }
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                .padding(vertical = 4.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.WbSunny, 
                    contentDescription = null, 
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFF9800)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("🌅 Sunrise: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(sunrise.format(formatter), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            if (isFallback) {
                Text("(approximate)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), fontSize = 10.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.WbTwilight, 
                    contentDescription = null, 
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFF5722)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("🌇 Sunset: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(sunset.format(formatter), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

@Composable
fun SunGridMarker(time: LocalTime, label: String, icon: ImageVector, iconTint: Color, isFallback: Boolean, hourHeight: Dp, is24Hour: Boolean) {
    val topOffset = calculateOffset(time, hourHeight)
    val pattern = if (is24Hour) "HH:mm" else "hh:mm a"
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val sectionLabel = if (label == "Sunrise") "Day Muhurat" else "Night Muhurat"
    
    val timeColumnWidth = 60.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = topOffset - 14.dp)
            .padding(start = 4.dp, end = 4.dp)
    ) {
        // Full width separator line with glow
        Canvas(modifier = Modifier.fillMaxWidth().height(2.dp).offset(y = 14.dp)) {
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
                .padding(start = timeColumnWidth, end = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.9f))
                    .border(2.dp, iconTint.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (label == "Sunrise") "🌅" else "🌇", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = sectionLabel,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.9f))
                    .border(2.dp, iconTint.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = iconTint)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${if (label == "Sunrise") "🌅" else "🌇"} $label: ${time.format(formatter)}${if (isFallback) " (approx)" else ""}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
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
            text = "NOW",
            color = if (color == Color.White) Color.Black else Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}
