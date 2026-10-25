package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
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
        color = if (isLoading) MaterialTheme.colorScheme.surfaceContainerHighest else scoreColor.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isLoading) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else scoreColor.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(enabled = !isLoading) { onBreakdownClick() }
            ) {
                // Transparent background mini-gauge + percentage text below
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(36.dp)
                        .background(Color.Transparent, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            SpeedometerCanvas(
                                score = score,
                                modifier = Modifier.size(32.dp, 16.dp),
                                strokeWidth = 3.5.dp,
                                showNeedle = true
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = "${score.toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = scoreColor
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(R.string.label_power_meter),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
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
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = { onHeatmapToggle() },
                    modifier = Modifier.size(30.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isHeatmapVisible) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = stringResource(R.string.cd_toggle_heatmap),
                        modifier = Modifier.size(16.dp),
                        tint = if (isHeatmapVisible) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
