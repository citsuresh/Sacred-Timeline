package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.R

@Composable
fun PowerMeterRow(
    score: Float, // 0 to 100
    bandTitle: String,
    onBreakdownClick: () -> Unit,
    onPreviewClick: () -> Unit,
    onHeatmapToggle: () -> Unit,
    isHeatmapVisible: Boolean,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val scoreColor = when {
        isLoading -> Color.Gray
        score >= 75f -> Color(0xFF4CAF50) // Green
        score >= 50f -> Color(0xFFFF9800) // Orange/Gold
        else -> Color(0xFFE53935) // Red
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(enabled = !isLoading) { onBreakdownClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(scoreColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = score.toInt().toString(),
                            color = scoreColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.label_power_meter),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                    Text(
                        text = if (isLoading) "Calculating…" else bandTitle,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { /* TODO: Best window action */ }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.cd_find_best_window), modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { onPreviewClick() }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Schedule, contentDescription = stringResource(R.string.cd_preview_time), modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(
                    onClick = { onHeatmapToggle() },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isHeatmapVisible) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = stringResource(R.string.cd_toggle_heatmap),
                        modifier = Modifier.size(18.dp),
                        tint = if (isHeatmapVisible) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
