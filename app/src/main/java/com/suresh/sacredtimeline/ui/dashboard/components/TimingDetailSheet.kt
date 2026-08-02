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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.*
import com.suresh.sacredtimeline.ui.dashboard.TimelineViewModel
import com.suresh.sacredtimeline.ui.theme.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimingDetailSheet(
    timing: Timing,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    viewModel: TimelineViewModel = viewModel()
) {
    val timeFormat24h by viewModel.timeFormat24h.collectAsState(initial = false)
    val pattern = if (timeFormat24h) "HH:mm" else "hh:mm a"
    val timeFormatter = DateTimeFormatter.ofPattern(pattern)
    val containerColor = SacredTimelineColors.getTimingColor(timing)
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
                        when (timing) {
                            is NallaNeram -> Icon(Icons.Default.Star, contentDescription = null, tint = contentColor)
                            is Hora -> Icon(Icons.Default.Today, contentDescription = null, tint = contentColor)
                            is GowriNeram -> Icon(Icons.Default.Brightness4, contentDescription = null, tint = contentColor)
                            is SpecialPeriod -> {
                                if (timing.name == "Yama") {
                                    Image(
                                        painter = painterResource(R.drawable.ic_yama_bull),
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp)
                                    )
                                } else {
                                    val iconPainter = when (timing.name) {
                                        "Rahu" -> painterResource(R.drawable.ic_rahu)
                                        "Kuli Dawn", "Kuli Dusk" -> painterResource(R.drawable.ic_saturn)
                                        else -> null
                                    }
                                    if (iconPainter != null) {
                                        Icon(iconPainter, contentDescription = null, tint = contentColor, modifier = Modifier.size(32.dp))
                                    } else {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = contentColor)
                                    }
                                }
                            }
                            else -> Icon(Icons.Default.Info, contentDescription = null, tint = contentColor)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = timing.tamilName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (timing) {
                            is Hora -> "${timing.name} Hora"
                            is GowriNeram -> "${timing.name} Gowri"
                            else -> timing.name
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            HorizontalDivider()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("From", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(timing.startTime.format(timeFormatter), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("To", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(timing.endTime.format(timeFormatter), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            
            if (timing is Hora) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = when (timing.compatibility) {
                        HoraCompatibility.FAVORABLE -> CompatibilityFavorable.copy(alpha = 0.1f)
                        HoraCompatibility.CONFLICTING -> CompatibilityConflicting.copy(alpha = 0.1f)
                        HoraCompatibility.NEUTRAL -> CompatibilityNeutral.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        val (icon, tint) = when (timing.compatibility) {
                            HoraCompatibility.FAVORABLE -> Icons.Default.CheckCircle to CompatibilityFavorable
                            HoraCompatibility.CONFLICTING -> Icons.Default.Cancel to CompatibilityConflicting
                            HoraCompatibility.NEUTRAL -> Icons.Default.RadioButtonUnchecked to CompatibilityNeutral
                        }
                        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp).padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = Metadata.getHoraGuidance(timing.name, timing.compatibility),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Strategic Activities", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                val activities = Metadata.getHoraStrategicActivities(timing.name, timing.compatibility)
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
            
            if (timing.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Significance", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = timing.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
            }
        }
    }
}
