package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
fun SunGridMarker(
    time: LocalTime, 
    label: String, 
    icon: ImageVector? = null, 
    painter: androidx.compose.ui.graphics.painter.Painter? = null,
    iconTint: Color, 
    hourHeight: Dp, 
    is24Hour: Boolean
) {
    val topOffset = calculateOffset(time, hourHeight)
    val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm")
    
    val displayTime = if (is24Hour) {
        time.format(timeFormatter)
    } else {
        val amPm = if (time.hour < 12) stringResource(R.string.label_am) else stringResource(R.string.label_pm)
        "${time.format(timeFormatter)} $amPm"
    }

    val sectionLabel = if (label == "Sunrise") stringResource(R.string.label_day_muhurat) else stringResource(R.string.label_night_muhurat)
    
    val timeColumnWidth = 50.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = topOffset - 18.dp)
            .padding(start = 4.dp, end = 4.dp)
            .heightIn(min = 36.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.Center)) {
            val drawTint = if (iconTint != Color.Unspecified) iconTint else Color(0xFFFF9800)
            drawRect(
                color = drawTint.copy(alpha = 0.3f),
                topLeft = Offset(0f, -4f),
                size = Size(size.width, 10f)
            )
            drawLine(
                color = drawTint,
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
            val drawTint = if (iconTint != Color.Unspecified) iconTint else Color(0xFFFF9800)
            Surface(
                color = Color.Black.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, drawTint.copy(alpha = 0.8f))
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

            Surface(
                color = Color.Black.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, drawTint.copy(alpha = 0.8f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (painter != null) {
                        Image(painter = painter, contentDescription = null, modifier = Modifier.size(14.dp))
                    } else if (icon != null) {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = iconTint)
                    }
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
