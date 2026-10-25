package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun AutoScrollingRow(
    modifier: Modifier = Modifier,
    resetKey: Any? = null,
    content: @Composable RowScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    
    LaunchedEffect(resetKey) {
        scrollState.scrollTo(0)
    }
    
    LaunchedEffect(scrollState.maxValue) {
        if (scrollState.maxValue > 0) {
            while (isActive) {
                if (!scrollState.isScrollInProgress) {
                    try {
                        delay(1000)
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
                            delay(1000)
                            if (!scrollState.isScrollInProgress) {
                                scrollState.scrollTo(0)
                            }
                        }
                    } catch (e: Exception) {
                        delay(500)
                    }
                } else {
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
    abhijitMuhurtham: com.suresh.sacredtimeline.model.Muhurtham?,
    brahmaMuhurtham: com.suresh.sacredtimeline.model.Muhurtham? = null,
    chandrashtamam: List<com.suresh.sacredtimeline.model.ChandrashtamamTiming> = emptyList(),
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
    
    val isValarpiraiMain = pakshaResId == R.string.paksha_valarpirai
    val showPiraiWithIcon = showPirai && paksha.isNotEmpty()

    val surfaceModifier = Modifier
        .fillMaxWidth()
        .animateContentSize()

    @Composable
    fun HeaderItems(isMarquee: Boolean) {
        if (isMarquee) {
            AutoScrollingRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 16.dp),
                resetKey = viewDate
            ) {
                if (isSubhaMuhurtham) {
                    MuhurthamItem(R.string.muhurtham_subha, LocalTime.MIN, LocalTime.MAX, null, Color(0xFF1976D2), is24Hour) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_subha, LocalTime.MIN, LocalTime.MAX))
                    }
                    Text("  •  ", color = Color.Gray)
                }
                specialEvents.forEach { resId ->
                    SpecialEventItem(resId, isValarpiraiMain, sunset, is24Hour) { start, end ->
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
                    if (item != nakshatras.last() || chandrashtamam.isNotEmpty()) Text("  •  ", color = Color.Gray)
                }
                chandrashtamam.forEach { item ->
                    val label = Metadata.getChandrashtamamLabel(item, androidx.compose.ui.platform.LocalContext.current)
                    val range = formatTransitRange(item.startTimeInstant, item.endTimeInstant, viewDate, is24Hour)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.TimelineTiming(item)) }
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (range.isNotEmpty()) "$label $range" else label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            maxLines = 1
                        )
                    }
                    if (item != chandrashtamam.last()) Text("  •  ", color = Color.Gray)
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
                    SpecialEventItem(resId, isValarpiraiMain, sunset, is24Hour, maxLines = Int.MAX_VALUE) { start, end ->
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
                chandrashtamam.forEach { item ->
                    val label = Metadata.getChandrashtamamLabel(item, androidx.compose.ui.platform.LocalContext.current)
                    val range = formatTransitRange(item.startTimeInstant, item.endTimeInstant, viewDate, is24Hour)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.TimelineTiming(item)) }
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (range.isNotEmpty()) "$label $range" else label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                }
                if (showBrahmaMuhurtham && brahmaMuhurtham != null) {
                    MuhurthamItem(R.string.muhurtham_brahma, brahmaMuhurtham.startTime, brahmaMuhurtham.endTime, Icons.Default.Star, Color(0xFFE91E63), is24Hour, maxLines = Int.MAX_VALUE) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_brahma, brahmaMuhurtham.startTime, brahmaMuhurtham.endTime))
                    }
                }
                if (showAbhijitMuhurtham && abhijitMuhurtham != null) {
                    MuhurthamItem(R.string.muhurtham_abhijit, abhijitMuhurtham.startTime, abhijitMuhurtham.endTime, Icons.Default.Star, Color(0xFF4CAF50), is24Hour, maxLines = Int.MAX_VALUE) {
                        onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_abhijit, abhijitMuhurtham.startTime, abhijitMuhurtham.endTime))
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
                                val tithiValueToUse = remember(tithiValue, pakshaDay, pakshaResId) {
                                    if (tithiValue != 0) tithiValue
                                    else if (pakshaResId == R.string.paksha_theipirai) pakshaDay + 15
                                    else pakshaDay
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        val primaryTithi = tithis.firstOrNull { it.value == tithiValueToUse } ?: tithis.firstOrNull()
                                        if (primaryTithi != null) {
                                            onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Lunar(
                                                primaryTithi, com.suresh.sacredtimeline.model.DashboardDetail.LunarType.PAKSHA
                                            ))
                                        }
                                    }
                                ) {
                                    MoonPhaseIcon(
                                        tithi = tithiValueToUse, 
                                        modifier = Modifier.size(16.dp),
                                        lightColor = Color.White, 
                                        darkColor = Color.Black, 
                                        strokeColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                                resetKey = viewDate,
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

            val eventsExist = specialEvents.isNotEmpty() || isSubhaMuhurtham || tithis.isNotEmpty() || nakshatras.isNotEmpty() || chandrashtamam.isNotEmpty()
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                onDetailClick(
                                    com.suresh.sacredtimeline.model.DashboardDetail.TimelineTiming(
                                        com.suresh.sacredtimeline.model.SolarTiming("Sunrise", "சூரிய உதயம்", sunrise, sunrise.plusMinutes(5), Auspiciousness.GREEN, "Local sunrise time.")
                                    )
                                )
                            }.padding(4.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_sun),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
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
                                .padding(horizontal = 8.dp),
                            resetKey = viewDate
                        ) {
                            if (hasBrahma) {
                                MuhurthamItem(R.string.muhurtham_brahma, brahmaMuhurtham.startTime, brahmaMuhurtham.endTime, Icons.Default.Star, Color(0xFFE91E63), is24Hour) {
                                    onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_brahma, brahmaMuhurtham.startTime, brahmaMuhurtham.endTime))
                                }
                            }
                            if (hasBrahma && hasAbhijit) {
                                Text("  •  ", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            }
                            if (hasAbhijit) {
                                MuhurthamItem(R.string.muhurtham_abhijit, abhijitMuhurtham.startTime, abhijitMuhurtham.endTime, Icons.Default.Star, Color(0xFF4CAF50), is24Hour) {
                                    onDetailClick(com.suresh.sacredtimeline.model.DashboardDetail.Muhurtham(R.string.muhurtham_abhijit, abhijitMuhurtham.startTime, abhijitMuhurtham.endTime))
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    if (showSunset) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                onDetailClick(
                                    com.suresh.sacredtimeline.model.DashboardDetail.TimelineTiming(
                                        com.suresh.sacredtimeline.model.SolarTiming("Sunset", "சூரிய அஸ்தமனம்", sunset, sunset.plusMinutes(5), Auspiciousness.GREEN, "Local sunset time.")
                                    )
                                )
                            }.padding(4.dp)
                        ) {
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
