package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.*
import com.suresh.sacredtimeline.ui.dashboard.TimelineViewModel
import com.suresh.sacredtimeline.ui.theme.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardDetailSheet(
    detail: DashboardDetail,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    viewModel: TimelineViewModel = viewModel()
) {
    val timeFormat24h by viewModel.timeFormat24h.collectAsState(initial = false)
    val pattern = if (timeFormat24h) "HH:mm" else "hh:mm a"
    val timeFormatter = DateTimeFormatter.ofPattern(pattern)
    val context = LocalContext.current

    val containerColor = when (detail) {
        is DashboardDetail.TimelineTiming -> SacredTimelineColors.getTimingColor(detail.timing)
        is DashboardDetail.Lunar -> if (detail.type == DashboardDetail.LunarType.TITHI) Color(0xFFE1F5FE) else Color(0xFFF3E5F5)
        is DashboardDetail.SpecialEvent -> Color(0xFFFFFDE7)
        is DashboardDetail.Muhurtham -> when (detail.title) {
            R.string.muhurtham_brahma -> Color(0xFFFCE4EC)
            R.string.muhurtham_abhijit -> Color(0xFFE8F5E9)
            else -> Color(0xFFE3F2FD)
        }
    }
    val contentColor = SacredTimelineColors.getContentColor(containerColor)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = containerColor,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        DetailIcon(detail, contentColor)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    val title = when (detail) {
                        is DashboardDetail.TimelineTiming -> {
                            val nameRes = when (val t = detail.timing) {
                                is Hora -> Metadata.getPlanetNameRes(t.name)
                                is NallaNeram -> Metadata.getSpecialNameRes("Nalla")
                                is GowriNeram -> Metadata.getGowriNameRes(t.name)
                                is SpecialPeriod -> Metadata.getSpecialNameRes(t.name)
                                is Muhurtham -> Metadata.getMuhurthamNameRes(t.name)
                            }
                            stringResource(nameRes)
                        }
                        is DashboardDetail.Lunar -> stringResource(detail.item.resId)
                        is DashboardDetail.SpecialEvent -> stringResource(detail.resId)
                        is DashboardDetail.Muhurtham -> stringResource(detail.title)
                    }
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val subTitle = when (detail) {
                        is DashboardDetail.TimelineTiming -> {
                            when (detail.timing) {
                                is Hora -> stringResource(R.string.nav_hora)
                                is GowriNeram -> stringResource(R.string.nav_gowri_neram)
                                else -> ""
                            }
                        }
                        is DashboardDetail.Lunar -> {
                            when (detail.type) {
                                DashboardDetail.LunarType.TITHI -> stringResource(R.string.label_tithi)
                                DashboardDetail.LunarType.NAKSHATRA -> stringResource(R.string.label_nakshatra)
                                DashboardDetail.LunarType.PAKSHA -> stringResource(R.string.label_paksha)
                            }
                        }
                        is DashboardDetail.SpecialEvent -> {
                            val months = listOf(
                                R.string.month_chithirai, R.string.month_vaikasi, R.string.month_aani,
                                R.string.month_aadi, R.string.month_avani, R.string.month_purattasi,
                                R.string.month_aippasi, R.string.month_karthigai, R.string.month_margazhi,
                                R.string.month_thai, R.string.month_maasi, R.string.month_panguni
                            )
                            if (detail.resId in months) stringResource(R.string.label_tamil_month) else ""
                        }
                        else -> ""
                    }
                    if (subTitle.isNotEmpty()) {
                        Text(
                            text = subTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timing Row
            val startTimeStr = when (detail) {
                is DashboardDetail.TimelineTiming -> detail.timing.startTime.format(timeFormatter)
                is DashboardDetail.Lunar -> detail.item.startTime?.atZone(ZoneId.systemDefault())?.format(timeFormatter) ?: ""
                is DashboardDetail.SpecialEvent -> detail.startTime?.format(timeFormatter) ?: ""
                is DashboardDetail.Muhurtham -> detail.startTime.format(timeFormatter)
            }
            val endTimeStr = when (detail) {
                is DashboardDetail.TimelineTiming -> detail.timing.endTime.format(timeFormatter)
                is DashboardDetail.Lunar -> detail.item.endTime?.atZone(ZoneId.systemDefault())?.format(timeFormatter) ?: ""
                is DashboardDetail.SpecialEvent -> detail.endTime?.format(timeFormatter) ?: ""
                is DashboardDetail.Muhurtham -> detail.endTime.format(timeFormatter)
            }

            if (startTimeStr.isNotEmpty() || endTimeStr.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (startTimeStr.isNotEmpty()) {
                        Column {
                            Text(stringResource(R.string.label_from), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(startTimeStr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (endTimeStr.isNotEmpty()) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(stringResource(R.string.label_to), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(endTimeStr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Guidance / Significance
            val guidance = when (detail) {
                is DashboardDetail.TimelineTiming -> {
                    if (detail.timing is Hora) {
                        Metadata.getHoraGuidance(detail.timing.name, detail.timing.compatibility, context)
                    } else ""
                }
                else -> ""
            }
            
            if (guidance.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = if (detail is DashboardDetail.TimelineTiming && detail.timing is Hora) {
                        when (detail.timing.compatibility) {
                            HoraCompatibility.FAVORABLE -> CompatibilityFavorable.copy(alpha = 0.1f)
                            HoraCompatibility.CONFLICTING -> CompatibilityConflicting.copy(alpha = 0.1f)
                            HoraCompatibility.NEUTRAL -> CompatibilityNeutral.copy(alpha = 0.1f)
                        }
                    } else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        if (detail is DashboardDetail.TimelineTiming && detail.timing is Hora) {
                            val (icon, tint) = when (detail.timing.compatibility) {
                                HoraCompatibility.FAVORABLE -> Icons.Default.CheckCircle to CompatibilityFavorable
                                HoraCompatibility.CONFLICTING -> Icons.Default.Cancel to CompatibilityConflicting
                                HoraCompatibility.NEUTRAL -> Icons.Default.RadioButtonUnchecked to CompatibilityNeutral
                            }
                            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Text(
                            text = guidance,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Strategic Activities
            if (detail is DashboardDetail.TimelineTiming && detail.timing is Hora) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.label_strategic_activities), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                val activities = Metadata.getHoraStrategicActivities(detail.timing.name, detail.timing.compatibility, context)
                activities.forEach { activity ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = activity, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            // General Significance / Description
            val description = when (detail) {
                is DashboardDetail.TimelineTiming -> {
                    when (val t = detail.timing) {
                        is Hora -> Metadata.getPlanetQuality(t.name, context)
                        is GowriNeram -> Metadata.getGowriDescription(t.name, context)
                        is SpecialPeriod -> Metadata.getSpecialDescription(t.name, context)
                        is NallaNeram -> Metadata.getSpecialDescription("Nalla", context)
                        is Muhurtham -> Metadata.getSpecialDescription(t.name, context)
                    }
                }
                is DashboardDetail.Lunar -> {
                    when (detail.type) {
                        DashboardDetail.LunarType.TITHI -> context.getString(Metadata.getTithiDescriptionRes(detail.item.value))
                        DashboardDetail.LunarType.NAKSHATRA -> context.getString(Metadata.getNakshatraDescriptionRes(detail.item.value))
                        DashboardDetail.LunarType.PAKSHA -> context.getString(R.string.desc_paksha)
                    }
                }
                is DashboardDetail.SpecialEvent -> context.getString(Metadata.getEventDescriptionRes(detail.resId))
                is DashboardDetail.Muhurtham -> context.getString(Metadata.getMuhurthamDescriptionRes(detail.title))
            }
            
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.label_significance), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun DetailIcon(detail: DashboardDetail, tint: Color) {
    when (detail) {
        is DashboardDetail.TimelineTiming -> {
            when (val t = detail.timing) {
                is NallaNeram -> Icon(Icons.Default.Star, contentDescription = null, tint = tint)
                is Hora -> Icon(Icons.Default.Today, contentDescription = null, tint = tint)
                is GowriNeram -> Icon(Icons.Default.Brightness4, contentDescription = null, tint = tint)
                is Muhurtham -> Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = tint)
                is SpecialPeriod -> {
                    if (t.name == "Yama") {
                        Image(
                            painter = painterResource(R.drawable.ic_yama_bull),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        val iconPainter = when (t.name) {
                            "Rahu" -> painterResource(R.drawable.ic_rahu)
                            "Kuli Dawn", "Kuli Dusk" -> painterResource(R.drawable.ic_saturn)
                            else -> null
                        }
                        if (iconPainter != null) {
                            Icon(iconPainter, contentDescription = null, tint = tint, modifier = Modifier.size(32.dp))
                        } else {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = tint)
                        }
                    }
                }
            }
        }
        is DashboardDetail.Lunar -> {
            if (detail.type == DashboardDetail.LunarType.TITHI) {
                MoonPhaseIcon(tithi = detail.item.value, modifier = Modifier.size(32.dp), lightColor = Color.White, darkColor = Color.Black, strokeColor = Color.Black)
            } else {
                Icon(Icons.Default.Stars, contentDescription = null, tint = tint, modifier = Modifier.size(32.dp))
            }
        }
        is DashboardDetail.SpecialEvent -> {
            when (detail.resId) {
                R.string.event_pradosham -> NandiIcon(modifier = Modifier.size(32.dp), light = true)
                R.string.event_sivaratri -> ShivaIcon(modifier = Modifier.size(32.dp))
                else -> Icon(Icons.Default.Celebration, contentDescription = null, tint = tint)
            }
        }
        is DashboardDetail.Muhurtham -> {
            when (detail.title) {
                R.string.muhurtham_subha -> KalashIcon(color = Color(0xFF1976D2), modifier = Modifier.size(32.dp))
                else -> Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = tint)
            }
        }
    }
}
