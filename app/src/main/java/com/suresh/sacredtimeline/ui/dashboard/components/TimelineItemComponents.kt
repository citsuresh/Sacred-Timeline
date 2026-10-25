package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            fontSize = if (maxLines == 1) 10.sp else 11.sp
        )
    }
}

@Composable
fun SpecialEventItem(
    resId: Int,
    isWhite: Boolean,
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
                NandiIcon(modifier = Modifier.size(16.dp), isWhite = isWhite)
                Spacer(modifier = Modifier.width(4.dp))
            }
            R.string.event_sivaratri -> {
                ShivaIcon(modifier = Modifier.size(16.dp), isWhite = isWhite)
                Spacer(modifier = Modifier.width(4.dp))
            }
            R.string.event_vinayagar_chaturthi, R.string.event_sankatahara_chaturthi -> {
                GaneshaIcon(modifier = Modifier.size(16.dp), isWhite = isWhite)
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
        val now = java.time.ZonedDateTime.now(zone)
        
        if (startTime != null && endTime != null && now.isAfter(startTime) && now.isBefore(endTime)) {
            return stringResource(R.string.label_till, formatWithAmPm(endTime.toLocalTime()))
        }

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

    val onSurface = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        if (type == com.suresh.sacredtimeline.model.DashboardDetail.LunarType.TITHI || type == com.suresh.sacredtimeline.model.DashboardDetail.LunarType.PAKSHA) {
            when (item.value) {
                4, 19 -> GaneshaIcon(modifier = Modifier.size(16.dp), isWhite = item.value <= 15)
                6, 21 -> VelIcon(modifier = Modifier.size(18.dp), isWhite = item.value <= 15)
                else -> MoonPhaseIcon(
                    tithi = item.value, 
                    modifier = Modifier.size(12.dp),
                    lightColor = Color.White, 
                    darkColor = Color.Black, 
                    strokeColor = onSurface
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

@Composable
fun formatTransitRange(start: java.time.Instant?, end: java.time.Instant?, viewDate: java.time.LocalDate, is24Hour: Boolean): String {
    val zone = java.time.ZoneId.systemDefault()
    val now = java.time.ZonedDateTime.now(zone)
    
    val startTime = start?.atZone(zone)
    val endTime = end?.atZone(zone)

    val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm")
    val amLabel = stringResource(R.string.label_am)
    val pmLabel = stringResource(R.string.label_pm)

    fun formatWithAmPm(time: LocalTime): String {
        if (is24Hour) return time.format(timeFormatter)
        val period = if (time.hour < 12) amLabel else pmLabel
        return "${time.format(timeFormatter)} $period"
    }

    val today = java.time.LocalDate.now()
    if (viewDate == today && startTime != null && endTime != null && now.toInstant().isAfter(startTime.toInstant()) && now.toInstant().isBefore(endTime.toInstant())) {
         return "(" + stringResource(R.string.label_till, formatWithAmPm(endTime.toLocalTime())) + ")"
    }

    val startStr = when {
        startTime == null -> null
        startTime.toLocalDate() == viewDate -> stringResource(R.string.label_starts_at, formatWithAmPm(startTime.toLocalTime()))
        startTime.toLocalDate() == viewDate.minusDays(1) -> stringResource(R.string.label_started_yesterday_at, formatWithAmPm(startTime.toLocalTime()))
        else -> {
            val df = DateTimeFormatter.ofPattern("MMM d")
            "Started ${startTime.format(df)} ${formatWithAmPm(startTime.toLocalTime())}"
        }
    }

    val endStr = when {
        endTime == null -> null
        endTime.toLocalDate() == viewDate -> stringResource(R.string.label_ends_at, formatWithAmPm(endTime.toLocalTime()))
        endTime.toLocalDate() == viewDate.plusDays(1) -> stringResource(R.string.label_ends_tomorrow_at, formatWithAmPm(endTime.toLocalTime()))
        else -> {
            val df = DateTimeFormatter.ofPattern("MMM d")
            "Ends ${endTime.format(df)} ${formatWithAmPm(endTime.toLocalTime())}"
        }
    }

    return if (startStr != null && endStr != null) {
        "($startStr - $endStr)"
    } else if (startStr != null) {
        "($startStr)"
    } else if (endStr != null) {
        "($endStr)"
    } else ""
}
