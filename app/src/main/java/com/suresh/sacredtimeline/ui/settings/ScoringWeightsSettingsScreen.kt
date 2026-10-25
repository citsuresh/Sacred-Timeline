package com.suresh.sacredtimeline.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoringWeightsSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val tharaBala by viewModel.tharaBalaWeight.collectAsState(initial = 0.20f)
    val transit by viewModel.transitWeight.collectAsState(initial = 0.20f)
    val dasa by viewModel.dasaWeight.collectAsState(initial = 0.20f)
    val hora by viewModel.horaWeight.collectAsState(initial = 0.15f)
    val yoga by viewModel.yogaWeight.collectAsState(initial = 0.15f)
    val tithi by viewModel.tithiWeight.collectAsState(initial = 0.10f)

    val totalSum = tharaBala + transit + dasa + hora + yoga + tithi

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scoring Weights Configuration") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Adjust the relative contribution weight of each factor in the astrological power score calculation. (Recommended total sum: 100%)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (abs(totalSum - 1.0f) < 0.05f) 
                        MaterialTheme.colorScheme.surfaceContainerHighest 
                    else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Weight Sum:", fontWeight = FontWeight.Bold)
                    Text("${(totalSum * 100).toInt()}%", fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }

            WeightSlider("Thara Bala (Personal Star)", tharaBala) { viewModel.updateTharaBalaWeight(it) }
            WeightSlider("Transit Positions (Gochara)", transit) { viewModel.updateTransitWeight(it) }
            WeightSlider("Dasa / Bhukti Period", dasa) { viewModel.updateDasaWeight(it) }
            WeightSlider("Hora Quality", hora) { viewModel.updateHoraWeight(it) }
            WeightSlider("Yoga Quality", yoga) { viewModel.updateYogaWeight(it) }
            WeightSlider("Tithi & Paksha Quality", tithi) { viewModel.updateTithiWeight(it) }
        }
    }
}

@Composable
private fun WeightSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text("${(value * 100).toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..0.5f,
                steps = 50
            )
        }
    }
}
