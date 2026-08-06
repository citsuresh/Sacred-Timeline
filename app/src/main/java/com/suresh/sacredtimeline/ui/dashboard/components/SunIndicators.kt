package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.cos

@Composable
fun AutoScrollingRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    
    LaunchedEffect(scrollState.maxValue) {
        if (scrollState.maxValue > 0) {
            while (isActive) {
                // Only auto-scroll if the user is NOT currently interacting with it
                if (!scrollState.isScrollInProgress) {
                    try {
                        delay(1000) // Pause to read
                        if (!scrollState.isScrollInProgress) {
                            val remainingDistance = scrollState.maxValue - scrollState.value
                            if (remainingDistance > 0) {
                                scrollState.animateScrollTo(
                                    value = scrollState.maxValue,
                                    animationSpec = androidx.compose.animation.core.tween(
                                        durationMillis = (remainingDistance * 25).coerceAtLeast(500),
                                        easing = androidx.compose.animation.core.LinearEasing
                                    )
                                )
                            }
                            delay(1000) // Pause at end
                            if (!scrollState.isScrollInProgress) {
                                scrollState.scrollTo(0)
                            }
                        }
                    } catch (e: Exception) {
                        delay(500)
                    }
                } else {
                    // Wait while user is scrolling
                    delay(500)
                }
            }
        }
    }

    Row(
        modifier = modifier.horizontalScroll(scrollState, enabled = true),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        content = content
    )
}

@Composable
fun MoonPhaseIcon(
    tithi: Int, 
    modifier: Modifier = Modifier, 
    lightColor: Color = Color.White, 
    darkColor: Color = Color.Black,
    strokeColor: Color = Color.Black
) {
    Canvas(modifier = modifier) {
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

@Composable
fun MuhurthamItem(
    resId: Int,
    start: LocalTime?,
    end: LocalTime?,
    icon: ImageVector?,
    color: Color,
    is24Hour: Boolean,
    maxLines: Int = 1,
    onClick: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm")
    val amLabel = stringResource(R.string.label_am)
    val pmLabel = stringResource(R.string.label_pm)

    fun formatWithAmPm(time: LocalTime): String {
        if (is24Hour) return time.format(timeFormatter)
        val period = if (time.hour < 12) amLabel else pmLabel
        return "${time.format(timeFormatter)} $period"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = color)
        } else if (resId == R.string.muhurtham_subha) {
            KalashIcon(color = color)
        }
        Spacer(modifier = Modifier.width(4.dp))
        val text = if (start != null && end != null && start != LocalTime.MIN) {
            "${stringResource(resId)}: ${formatWithAmPm(start)} - ${formatWithAmPm(end)}"
        } else {
            stringResource(resId)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = maxLines
        )
    }
}

@Composable
fun SpecialEventItem(
    resId: Int,
    tithiValue: Int,
    sunset: LocalTime,
    is24Hour: Boolean,
    maxLines: Int = 1,
    onClick: (LocalTime?, LocalTime?) -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm")
    val amLabel = stringResource(R.string.label_am)
    val pmLabel = stringResource(R.string.label_pm)

    fun formatWithAmPm(time: LocalTime): String {
        if (is24Hour) return time.format(timeFormatter)
        val period = if (time.hour < 12) amLabel else pmLabel
        return "${time.format(timeFormatter)} $period"
    }

    val color = when (resId) {
        R.string.event_independence_day, R.string.event_republic_day, R.string.event_milad_un_nabi -> Color.Red
        else -> Color(0xFFC5A000)
    }
    
    val eventStartEnd = if (resId == R.string.event_pradosham) {
        sunset.minusMinutes(90) to sunset
    } else null

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick(eventStartEnd?.first, eventStartEnd?.second) }
    ) {
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
        val eventText = if (eventStartEnd != null) {
            "${stringResource(resId)} (${formatWithAmPm(eventStartEnd.first)} - ${formatWithAmPm(eventStartEnd.second)})"
        } else {
            stringResource(resId)
        }
        Text(
            text = eventText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = maxLines
        )
    }
}

@Composable
fun LunarItem(
    item: com.suresh.sacredtimeline.model.LunarInterval,
    type: com.suresh.sacredtimeline.model.DashboardDetail.LunarType,
    viewDate: java.time.LocalDate,
    is24Hour: Boolean,
    maxLines: Int = 1,
    onClick: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm")
    val amLabel = stringResource(R.string.label_am)
    val pmLabel = stringResource(R.string.label_pm)

    fun formatWithAmPm(time: LocalTime): String {
        if (is24Hour) return time.format(timeFormatter)
        val period = if (time.hour < 12) amLabel else pmLabel
        return "${time.format(timeFormatter)} $period"
    }

    @Composable
    fun formatLunarRange(start: java.time.Instant?, end: java.time.Instant?): String {
        val zone = java.time.ZoneId.systemDefault()
        val startTime = start?.atZone(zone)
        val endTime = end?.atZone(zone)
        
        val startStr = when {
            startTime == null -> null
            startTime.toLocalDate().isBefore(viewDate) -> stringResource(R.string.label_started_yesterday_at, formatWithAmPm(startTime.toLocalTime()))
            else -> stringResource(R.string.label_starts_at, formatWithAmPm(startTime.toLocalTime()))
        }
        
        val endStr = when {
            endTime == null -> null
            endTime.toLocalDate().isAfter(viewDate) -> stringResource(R.string.label_ends_tomorrow_at, formatWithAmPm(endTime.toLocalTime()))
            else -> stringResource(R.string.label_ends_at, formatWithAmPm(endTime.toLocalTime()))
        }
        
        return if (startStr != null && endStr != null) {
            "($startStr - $endStr)"
        } else if (startStr != null) {
            "($startStr)"
        } else if (endStr != null) {
            "($endStr)"
        } else ""
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        if (type == com.suresh.sacredtimeline.model.DashboardDetail.LunarType.TITHI || type == com.suresh.sacredtimeline.model.DashboardDetail.LunarType.PAKSHA) {
            when (item.value) {
                4 -> GaneshaIcon(modifier = Modifier.size(16.dp), light = true)
                19 -> GaneshaIcon(modifier = Modifier.size(16.dp), light = false)
                6 -> VelIcon(modifier = Modifier.size(18.dp), lightColor = Color.White, darkColor = Color.Black, strokeColor = Color.Black)
                21 -> VelIcon(modifier = Modifier.size(18.dp), lightColor = Color.Black, darkColor = Color.White, strokeColor = Color.White)
                else -> MoonPhaseIcon(
                    tithi = item.value, 
                    modifier = Modifier.size(12.dp),
                    lightColor = Color.White, 
                    darkColor = Color.Black, 
                    strokeColor = Color.Black
                )
            }
        } else {
            if (item.resId == R.string.star_3) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF9800))
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        val name = stringResource(item.resId)
        val range = formatLunarRange(item.startTime, item.endTime)
        Text(
            text = if (range.isNotEmpty()) "$name $range" else name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = maxLines
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SunTimesDisplay(
    sunrise: LocalTime,
    sunset: LocalTime,
    viewDate: java.time.LocalDate,
    tamilDay: Int,
    tamilMonthResId: Int,
    tamilYearResId: Int,
    pakshaResId: Int,
    pakshaDay: Int,
    tithis: List<com.suresh.sacredtimeline.model.LunarInterval> = emptyList(),
    nakshatras: List<com.suresh.sacredtimeline.model.LunarInterval> = emptyList(),
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
    showAbhijitMuhurtham: Boolean = false,
    isExpanded: Boolean = false,
    onToggleExpanded: (Boolean) -> Unit = {},
    isFallback: Boolean,
    isLandscape: Boolean,
    is24Hour: Boolean,
    onDetailClick: (com.suresh.sacredtimeline.model.DashboardDetail) -> Unit = {}
) {
    val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm")
    val amLabel = stringResource(R.string.label_am)
    val pmLabel = stringResource(R.string.label_pm)

    fun formatWithAmPm(time: LocalTime): String {
        if (is24Hour) return time.format(timeFormatter)
        val period = if (time.hour < 12) amLabel else pmLabel
        return "${time.format(timeFormatter)} $period"
    }

    val tamilMonth = if (tamilMonthResId != 0) stringResource(tamilMonthResId) else ""
    val tamilYear = if (tamilYearResId != 0) stringResource(tamilYearResId) else ""
    val paksha = if (pakshaResId != 0) stringResource(pakshaResId) else ""

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

    val surfaceModifier = Modifier
        .fillMaxWidth()
        .animateContentSize()

    @Composable
    fun HeaderItems(isMarquee: Boolean) {
        if (isMarquee) {
            AutoScrollingRow(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 16.dp)) {
                if (isSubhaMuhurtham) {
                    MuhurthamItem(R.string.muhurtham_subha, LocalTime.MIN, LocalTime.MAX, null, Color(0xFF1976D2), is24Hour) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_subha, LocalTime.MIN, LocalTime.MAX))
                    }
                    Text("  •  ", color = Color.Gray)
                }
                specialEvents.forEach { resId ->
                    SpecialEventItem(resId, tithiValue, sunset, is24Hour) { start, end ->
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.SpecialEvent(resId, start, end))
                    }
                    Text("  •  ", color = Color.Gray)
                }
                tithis.forEach { item ->
                    LunarItem(item, com.suresh.sacredtimeline.model.DashboardDetail.LunarType.TITHI, viewDate, is24Hour) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Lunar(item, com.suresh.sacredtimeline.model.DashboardDetail.LunarType.TITHI))
                    }
                    Text("  •  ", color = Color.Gray)
                }
                nakshatras.forEach { item ->
                    LunarItem(item, com.suresh.sacredtimeline.model.DashboardDetail.LunarType.NAKSHATRA, viewDate, is24Hour) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Lunar(item, com.suresh.sacredtimeline.model.DashboardDetail.LunarType.NAKSHATRA))
                    }
                    if (item != nakshatras.last()) Text("  •  ", color = Color.Gray)
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isSubhaMuhurtham) {
                    MuhurthamItem(R.string.muhurtham_subha, LocalTime.MIN, LocalTime.MAX, null, Color(0xFF1976D2), is24Hour, maxLines = Int.MAX_VALUE) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_subha, LocalTime.MIN, LocalTime.MAX))
                    }
                }
                specialEvents.forEach { resId ->
                    SpecialEventItem(resId, tithiValue, sunset, is24Hour, maxLines = Int.MAX_VALUE) { start, end ->
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.SpecialEvent(resId, start, end))
                    }
                }
                tithis.forEach { item ->
                    LunarItem(item, com.suresh.sacredtimeline.model.DashboardDetail.LunarType.TITHI, viewDate, is24Hour, maxLines = Int.MAX_VALUE) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Lunar(item, com.suresh.sacredtimeline.model.DashboardDetail.LunarType.TITHI))
                    }
                }
                nakshatras.forEach { item ->
                    LunarItem(item, com.suresh.sacredtimeline.model.DashboardDetail.LunarType.NAKSHATRA, viewDate, is24Hour, maxLines = Int.MAX_VALUE) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Lunar(item, com.suresh.sacredtimeline.model.DashboardDetail.LunarType.NAKSHATRA))
                    }
                }
                if (showBrahmaMuhurtham && brahmaMuhurtham != null) {
                    MuhurthamItem(R.string.muhurtham_brahma, brahmaMuhurtham.first, brahmaMuhurtham.second, Icons.Default.Star, Color(0xFFE91E63), is24Hour, maxLines = Int.MAX_VALUE) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_brahma, brahmaMuhurtham.first, brahmaMuhurtham.second))
                    }
                }
                if (showAbhijitMuhurtham && abhijitMuhurtham != null) {
                    MuhurthamItem(R.string.muhurtham_abhijit, abhijitMuhurtham.first, abhijitMuhurtham.second, Icons.Default.Star, Color(0xFF4CAF50), is24Hour, maxLines = Int.MAX_VALUE) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_abhijit, abhijitMuhurtham.first, abhijitMuhurtham.second))
                    }
                }
            }
        }
    }

    Surface(
        color = if (isLandscape) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        shape = if (isLandscape) RoundedCornerShape(8.dp) else RoundedCornerShape(0.dp),
        modifier = surfaceModifier,
        border = if (isLandscape) BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)) else null
    ) {
        Column {
            if (tamilDateMain.isNotEmpty() || showPiraiWithIcon) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        val content: @Composable RowScope.() -> Unit = {
                            if (showTamilDate) {
                                Text(
                                    text = if (tamilDay > 0) "$tamilDay $tamilMonth" else tamilMonth,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    modifier = Modifier.clickable {
                                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.SpecialEvent(tamilMonthResId))
                                    }
                                )
                            }
                            if (showTamilDate && showTamilYear && tamilYear.isNotEmpty()) {
                                Text(
                                    text = " - ",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                                )
                            }
                            if (showTamilYear && tamilYear.isNotEmpty()) {
                                Text(
                                    text = tamilYear,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    modifier = Modifier.clickable {
                                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.SpecialEvent(tamilYearResId))
                                    }
                                )
                            }
                            
                            if ((showTamilDate || showTamilYear) && showPiraiWithIcon) {
                                Text(
                                    text = " | ",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                                )
                            }
                            
                            if (showPiraiWithIcon) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        val primaryTithi = tithis.firstOrNull { it.value == tithiValue } ?: tithis.firstOrNull()
                                        if (primaryTithi != null) {
                                            onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Lunar(
                                                primaryTithi, com.suresh.sacredtimeline.model.DashboardDetail.LunarType.PAKSHA
                                            ))
                                        }
                                    }
                                ) {
                                    MoonPhaseIcon(
                                        tithi = tithiValue, 
                                        modifier = Modifier.size(16.dp),
                                        lightColor = Color.White, 
                                        darkColor = Color.Black, 
                                        strokeColor = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$paksha $pakshaDay",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        if (isExpanded) {
                            FlowRow(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .align(Alignment.Center),
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.Center
                            ) {
                                content()
                            }
                        } else {
                            AutoScrollingRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 32.dp, top = 4.dp, bottom = 4.dp),
                                content = content
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) stringResource(R.string.label_collapse) else stringResource(R.string.label_expand),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                                .size(24.dp)
                                .clickable { onToggleExpanded(!isExpanded) }
                        )
                    }
                }
            }

            val eventsExist = specialEvents.isNotEmpty() || isSubhaMuhurtham || tithis.isNotEmpty() || nakshatras.isNotEmpty()
            if (eventsExist) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = isExpanded,
                        label = "HeaderExpansion"
                    ) { expanded ->
                        HeaderItems(isMarquee = !expanded)
                    }
                }
            }

            if (showSunrise || showSunset || (showBrahmaMuhurtham && brahmaMuhurtham != null) || (showAbhijitMuhurtham && abhijitMuhurtham != null)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (showSunrise) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFF9800))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(formatWithAmPm(sunrise), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    val hasBrahma = showBrahmaMuhurtham && brahmaMuhurtham != null
                    val hasAbhijit = showAbhijitMuhurtham && abhijitMuhurtham != null
                    if (!isExpanded && (hasBrahma || hasAbhijit)) {
                        AutoScrollingRow(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            if (hasBrahma) {
                                MuhurthamItem(R.string.muhurtham_brahma, brahmaMuhurtham!!.first, brahmaMuhurtham!!.second, Icons.Default.Star, Color(0xFFE91E63), is24Hour) {
                                    onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_brahma, brahmaMuhurtham.first, brahmaMuhurtham.second))
                                }
                            }
                            if (hasBrahma && hasAbhijit) {
                                Text("  •  ", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            }
                            if (hasAbhijit) {
                                MuhurthamItem(R.string.muhurtham_abhijit, abhijitMuhurtham!!.first, abhijitMuhurtham!!.second, Icons.Default.Star, Color(0xFF4CAF50), is24Hour) {
                                    onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_abhijit, abhijitMuhurtham.first, abhijitMuhurtham.second))
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    if (showSunset) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(formatWithAmPm(sunset), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.WbTwilight, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFF5722))
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
