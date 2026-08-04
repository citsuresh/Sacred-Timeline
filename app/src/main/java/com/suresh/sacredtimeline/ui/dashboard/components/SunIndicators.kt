package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.Image
import kotlin.math.abs
import kotlin.math.cos

@Composable
fun MoonPhaseIcon(
    tithi: Int, 
    modifier: Modifier = Modifier, 
    lightColor: Color = Color.White, 
    darkColor: Color = Color.Black,
    strokeColor: Color = Color.Black
) {
    Canvas(modifier = modifier.size(16.dp)) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2, size.height / 2)
        val rect = Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius)

        // 1. Fill the background (Dark part)
        drawCircle(
            color = darkColor,
            radius = radius,
            center = center
        )

        val t = tithi % 30
        
        // 2. Draw the illuminated part
        if (t != 0) {
            val angleRad = Math.toRadians(t * 12.0)
            val innerWidth = radius * cos(angleRad).toFloat()
            val innerRect = Rect(center.x - abs(innerWidth), center.y - radius, center.x + abs(innerWidth), center.y + radius)
            
            val path = Path()
            if (t <= 15) {
                // Waxing: Right side lit
                path.addArc(rect, -90f, 180f)
                path.arcTo(
                    rect = innerRect,
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = if (innerWidth > 0) -180f else 180f,
                    forceMoveTo = false
                )
            } else {
                // Waning: Left side lit
                path.addArc(rect, 90f, 180f)
                path.arcTo(
                    rect = innerRect,
                    startAngleDegrees = -90f,
                    sweepAngleDegrees = if (innerWidth > 0) -180f else 180f,
                    forceMoveTo = false
                )
            }
            path.close()
            drawPath(path, lightColor)
        }

        // 3. Draw the thin black outline
        drawCircle(
            color = strokeColor,
            radius = radius,
            center = center,
            style = Stroke(width = 0.5.dp.toPx())
        )
    }
}

@Composable
fun KalashIcon(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        
        val potPath = Path().apply {
            moveTo(w * 0.5f, h * 0.95f)
            cubicTo(w * 0.2f, h * 0.95f, w * 0.15f, h * 0.7f, w * 0.15f, h * 0.6f)
            cubicTo(w * 0.15f, h * 0.5f, w * 0.35f, h * 0.45f, w * 0.35f, h * 0.42f)
            lineTo(w * 0.65f, h * 0.42f)
            cubicTo(w * 0.65f, h * 0.45f, w * 0.85f, h * 0.5f, w * 0.85f, h * 0.6f)
            cubicTo(w * 0.85f, h * 0.7f, w * 0.80f, h * 0.95f, w * 0.5f, h * 0.95f)
            close()
        }
        
        val rimPath = Path().apply {
            addRect(Rect(w * 0.3f, h * 0.38f, w * 0.7f, h * 0.43f))
        }
        
        val coconutPath = Path().apply {
            moveTo(w * 0.4f, h * 0.38f)
            cubicTo(w * 0.4f, h * 0.2f, w * 0.5f, h * 0.05f, w * 0.5f, h * 0.05f)
            cubicTo(w * 0.5f, h * 0.05f, w * 0.6f, h * 0.2f, w * 0.6f, h * 0.38f)
            close()
        }
        
        val leavesPath = Path().apply {
            moveTo(w * 0.35f, h * 0.38f)
            lineTo(w * 0.15f, h * 0.25f)
            lineTo(w * 0.4f, h * 0.35f)
            moveTo(w * 0.65f, h * 0.38f)
            lineTo(w * 0.85f, h * 0.25f)
            lineTo(w * 0.6f, h * 0.35f)
        }

        drawPath(potPath, color)
        drawPath(rimPath, color)
        drawPath(coconutPath, color)
        drawPath(leavesPath, color, style = Stroke(width = 0.8.dp.toPx()))
    }
}

@Composable
fun NandiIcon(modifier: Modifier = Modifier, light: Boolean) {
    Image(
        painter = painterResource(id = if (light) R.drawable.ic_nandi_white else R.drawable.ic_nandi_black),
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
fun ShivaIcon(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_shiva),
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
fun GaneshaIcon(modifier: Modifier = Modifier, light: Boolean) {
    Image(
        painter = painterResource(id = if (light) R.drawable.ic_ganesha_white else R.drawable.ic_ganesha_black),
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
fun VelIcon(modifier: Modifier = Modifier, lightColor: Color, darkColor: Color, strokeColor: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        
        // 1. Shaft (Pole)
        // Balanced thickness for visibility without being too bulky
        drawLine(
            color = strokeColor, 
            start = Offset(w * 0.5f, h * 0.5f), 
            end = Offset(w * 0.5f, h * 0.98f), 
            strokeWidth = 2.2.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        drawLine(
            color = lightColor, 
            start = Offset(w * 0.5f, h * 0.5f), 
            end = Offset(w * 0.5f, h * 0.98f), 
            strokeWidth = 1.0.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        
        // 2. Blade (Traditional leaf-shaped Vel)
        val blade = Path().apply {
            moveTo(w * 0.5f, h * 0.02f)
            cubicTo(w * 0.1f, h * 0.25f, w * 0.1f, h * 0.6f, w * 0.5f, h * 0.85f)
            cubicTo(w * 0.9f, h * 0.6f, w * 0.9f, h * 0.25f, w * 0.5f, h * 0.02f)
            close()
        }
        
        drawPath(blade, lightColor)
        // Reduced border thickness
        drawPath(blade, strokeColor, style = Stroke(width = 1.2.dp.toPx())) 
        
        // 3. Vibhuti lines
        val vibhutiColor = if (lightColor == Color.White) Color.Black else Color.White
        val lineW = w * 0.18f
        drawLine(vibhutiColor, start = Offset(w * 0.5f - lineW, h * 0.32f), end = Offset(w * 0.5f + lineW, h * 0.32f), strokeWidth = 0.8.dp.toPx())
        drawLine(vibhutiColor, start = Offset(w * 0.5f - lineW, h * 0.40f), end = Offset(w * 0.5f + lineW, h * 0.40f), strokeWidth = 0.8.dp.toPx())
        drawLine(vibhutiColor, start = Offset(w * 0.5f - lineW, h * 0.48f), end = Offset(w * 0.5f + lineW, h * 0.48f), strokeWidth = 0.8.dp.toPx())
        
        // 4. Tiny Dot (Kumkum)
        drawCircle(Color.Red, radius = 0.8.dp.toPx(), center = Offset(w * 0.5f, h * 0.40f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SunTimesDisplay(
    sunrise: LocalTime,
    sunset: LocalTime,
    tamilDay: Int,
    tamilMonthResId: Int,
    tamilYearResId: Int,
    pakshaResId: Int,
    pakshaDay: Int,
    tithiResId: Int,
    nakshatraResId: Int,
    tithiValue: Int,
    specialEvents: List<Int>,
    isSubhaMuhurtham: Boolean,
    abhijitMuhurtham: Pair<LocalTime, LocalTime>?,
    brahmaMuhurtham: Pair<LocalTime, LocalTime>? = null,
    showTamilDate: Boolean,
    showTamilYear: Boolean,
    showPirai: Boolean,
    showSunrise: Boolean = true,
    showSunset: Boolean = true,
    showBrahmaMuhurtham: Boolean = false,
    isFallback: Boolean,
    isLandscape: Boolean,
    is24Hour: Boolean
) {
    val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm")
    val amLabel = stringResource(R.string.label_am)
    val pmLabel = stringResource(R.string.label_pm)

    val tamilMonth = if (tamilMonthResId != 0) stringResource(tamilMonthResId) else ""
    val tamilYear = if (tamilYearResId != 0) stringResource(tamilYearResId) else ""
    val paksha = if (pakshaResId != 0) stringResource(pakshaResId) else ""
    val tithi = if (tithiResId != 0) stringResource(tithiResId) else ""
    val star = if (nakshatraResId != 0) stringResource(nakshatraResId) else ""

    val tamilDateMain = remember(showTamilDate, showTamilYear, tamilDay, tamilMonth, tamilYear) {
        buildString {
            val parts = mutableListOf<String>()
            val datePart = buildString {
                if (showTamilDate) {
                    if (tamilDay > 0) append("$tamilDay ")
                    append(tamilMonth)
                }
            }.trim()
            if (datePart.isNotEmpty()) parts.add(datePart)
            if (showTamilYear && tamilYear.isNotEmpty()) parts.add(tamilYear)
            append(parts.joinToString(" - "))
        }
    }
    
    val showPiraiWithIcon = showPirai && paksha.isNotEmpty()

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
                if (tamilDateMain.isNotEmpty() || showPiraiWithIcon) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (tamilDateMain.isNotEmpty()) {
                            Text(
                                text = tamilDateMain,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (tamilDateMain.isNotEmpty() && showPiraiWithIcon) {
                            Text(" | ", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), style = MaterialTheme.typography.labelSmall)
                        }
                        if (showPiraiWithIcon) {
                            MoonPhaseIcon(
                                tithi = tithiValue, 
                                lightColor = Color.White, 
                                darkColor = Color.Black, 
                                strokeColor = Color.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "$paksha $pakshaDay", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f))
                }
                
                if (specialEvents.isNotEmpty() || isSubhaMuhurtham || tithi.isNotEmpty() || star.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSubhaMuhurtham) {
                            KalashIcon(color = Color(0xFF1976D2))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.muhurtham_subha), style = MaterialTheme.typography.labelSmall, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                            Text(" • ", color = Color.Gray)
                        }
                        specialEvents.forEach { resId ->
                            when (resId) {
                                R.string.event_pradosham -> {
                                    NandiIcon(modifier = Modifier.size(16.dp), light = tithiValue <= 15)
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                R.string.event_sivaratri -> {
                                    ShivaIcon(modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                            Text(stringResource(resId), style = MaterialTheme.typography.labelSmall, color = Color(0xFFC5A000), fontWeight = FontWeight.Bold)
                            Text(" • ", color = Color.Gray)
                        }
                        if (tithi.isNotEmpty()) {
                            when (tithiValue) {
                                4 -> GaneshaIcon(modifier = Modifier.size(16.dp), light = true)
                                19 -> GaneshaIcon(modifier = Modifier.size(16.dp), light = false)
                                6 -> VelIcon(modifier = Modifier.size(18.dp), lightColor = Color.White, darkColor = Color.Black, strokeColor = Color.Black)
                                21 -> VelIcon(modifier = Modifier.size(18.dp), lightColor = Color.Black, darkColor = Color.White, strokeColor = Color.White)
                                else -> MoonPhaseIcon(
                                    tithi = tithiValue, 
                                    modifier = Modifier.size(12.dp),
                                    lightColor = Color.White,
                                    darkColor = Color.Black,
                                    strokeColor = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(tithi, style = MaterialTheme.typography.labelSmall)
                            Text(" • ", color = Color.Gray)
                        }
                        if (star.isNotEmpty()) {
                            if (nakshatraResId == R.string.star_3) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF9800))
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                            Text(star, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                if (showSunrise || showSunset || (showBrahmaMuhurtham && brahmaMuhurtham != null)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.basicMarquee()) {
                        if (showSunrise) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF9800))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(formatWithAmPm(sunrise), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            if (showSunset || (showBrahmaMuhurtham && brahmaMuhurtham != null)) Spacer(modifier = Modifier.width(16.dp))
                        }
                        if (showSunset) {
                            Icon(Icons.Default.WbTwilight, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF5722))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(formatWithAmPm(sunset), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            if (showBrahmaMuhurtham && brahmaMuhurtham != null) Spacer(modifier = Modifier.width(16.dp))
                        }
                        if (showBrahmaMuhurtham && brahmaMuhurtham != null) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFE91E63))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${stringResource(R.string.muhurtham_brahma)}: ${formatWithAmPm(brahmaMuhurtham.first)} - ${formatWithAmPm(brahmaMuhurtham.second)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE91E63)
                            )
                        }
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
        ) {
            if (tamilDateMain.isNotEmpty() || showPiraiWithIcon) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (tamilDateMain.isNotEmpty()) {
                            Text(
                                text = tamilDateMain,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        if (tamilDateMain.isNotEmpty() && showPiraiWithIcon) {
                            Text(
                                text = " | ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                            )
                        }
                        if (showPiraiWithIcon) {
                            MoonPhaseIcon(
                                tithi = tithiValue, 
                                lightColor = Color.White, 
                                darkColor = Color.Black, 
                                strokeColor = Color.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$paksha $pakshaDay",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            val eventsExist = specialEvents.isNotEmpty() || isSubhaMuhurtham || tithi.isNotEmpty() || star.isNotEmpty()
            if (eventsExist) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .basicMarquee(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isSubhaMuhurtham) {
                            KalashIcon(color = Color(0xFF1976D2))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.muhurtham_subha),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                            Text("  •  ", color = Color.Gray)
                        }
                        
                        specialEvents.forEach { resId ->
                            val color = when (resId) {
                                R.string.event_independence_day, R.string.event_republic_day, R.string.event_milad_un_nabi -> Color.Red
                                else -> Color(0xFFC5A000) 
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                when (resId) {
                                    R.string.event_pradosham -> {
                                        NandiIcon(modifier = Modifier.size(16.dp), light = tithiValue <= 15)
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    R.string.event_sivaratri -> {
                                        ShivaIcon(modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                }
                                Text(
                                    text = stringResource(resId),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                            }
                            Text("  •  ", color = Color.Gray)
                        }

                        if (tithi.isNotEmpty()) {
                            when (tithiValue) {
                                4 -> GaneshaIcon(modifier = Modifier.size(16.dp), light = true)
                                19 -> GaneshaIcon(modifier = Modifier.size(16.dp), light = false)
                                6 -> VelIcon(modifier = Modifier.size(18.dp), lightColor = Color.White, darkColor = Color.Black, strokeColor = Color.Black)
                                21 -> VelIcon(modifier = Modifier.size(18.dp), lightColor = Color.Black, darkColor = Color.White, strokeColor = Color.White)
                                else -> MoonPhaseIcon(
                                    tithi = tithiValue, 
                                    lightColor = Color.White, 
                                    darkColor = Color.Black, 
                                    strokeColor = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tithi,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text("  •  ", color = Color.Gray)
                        }

                        if (star.isNotEmpty()) {
                            if (nakshatraResId == R.string.star_3) {
                                Icon(
                                    Icons.Default.Star, 
                                    contentDescription = null, 
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFFFF9800)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = star,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (showSunrise || showSunset || (showBrahmaMuhurtham && brahmaMuhurtham != null)) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (showSunrise) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFF9800))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(formatWithAmPm(sunrise), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (showSunset) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbTwilight, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFF5722))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(formatWithAmPm(sunset), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (showBrahmaMuhurtham && brahmaMuhurtham != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFE91E63))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${stringResource(R.string.muhurtham_brahma)}: ${formatWithAmPm(brahmaMuhurtham.first)} - ${formatWithAmPm(brahmaMuhurtham.second)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE91E63)
                            )
                        }
                    }
                }
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
        Canvas(modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.Center)) {
            drawRect(
                color = iconTint.copy(alpha = 0.3f),
                topLeft = Offset(0f, -4f),
                size = Size(size.width, 10f)
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
