package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreBreakdownSheet(
    score: Float, // 0 to 100
    bandTitle: String,
    factorScores: Map<String, Float> = emptyMap(),
    onDismiss: () -> Unit,
    onNavigateToScoringWeights: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.title_power_score_breakdown),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateToScoringWeights) {
                        Icon(Icons.Default.Settings, contentDescription = "Scoring Weights Settings")
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp, 110.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        SpeedometerCanvas(
                            score = score,
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 22.dp,
                            showNeedle = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${score.toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = bandTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.label_activity_guidance),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val recommendationRes = when {
                score >= 80f -> R.string.rec_highly_favorable
                score >= 65f -> R.string.rec_favorable
                score >= 50f -> R.string.rec_neutral
                score >= 35f -> R.string.rec_caution
                else -> R.string.rec_inauspicious
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            ) {
                Text(
                    text = stringResource(recommendationRes),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.label_itemized_factor_contributors),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (factorScores.isNotEmpty()) {
                factorScores.forEach { (factorName, factorScore) ->
                    FactorContributorCard(
                        title = factorName,
                        subtitle = stringResource(R.string.subtitle_weighted_factor_score),
                        scoreBadge = "${factorScore.toInt()} pts"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                FactorContributorCard("Thara Balam (Personal Star)", "Favorable Tara Bala alignment.", "20 pts")
                Spacer(modifier = Modifier.height(8.dp))
                FactorContributorCard("Transit Influences (Jupiter)", "Supportive planetary transit position.", "18 pts")
                Spacer(modifier = Modifier.height(8.dp))
                FactorContributorCard("Dasa / Bhukti Period", "Favorable major/minor planetary period.", "16 pts")
                Spacer(modifier = Modifier.height(8.dp))
                FactorContributorCard("Hora Quality & Compatibility", "Auspicious planetary hour (Hora).", "15 pts")
                Spacer(modifier = Modifier.height(8.dp))
                FactorContributorCard("Yoga Quality", "Benefic daily Yoga.", "12 pts")
                Spacer(modifier = Modifier.height(8.dp))
                FactorContributorCard("Tithi & Paksha Quality", "Lunar day (Tithi) influence.", "8 pts")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FactorContributorCard(title: String, subtitle: String, scoreBadge: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = Color(0xFFC8E6C9),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = scoreBadge,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}
