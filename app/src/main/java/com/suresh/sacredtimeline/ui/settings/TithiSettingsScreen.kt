package com.suresh.sacredtimeline.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suresh.sacredtimeline.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TithiSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val enabledTithis by viewModel.enabledTithis.collectAsState()

    val tithiOptions = listOf(
        "TITHI_1" to R.string.tithi_1,
        "TITHI_2" to R.string.tithi_2,
        "TITHI_3" to R.string.tithi_3,
        "TITHI_4" to R.string.tithi_4,
        "TITHI_5" to R.string.tithi_5,
        "TITHI_6" to R.string.tithi_6,
        "TITHI_7" to R.string.tithi_7,
        "TITHI_8" to R.string.tithi_8,
        "TITHI_9" to R.string.tithi_9,
        "TITHI_10" to R.string.tithi_10,
        "TITHI_11" to R.string.tithi_11,
        "TITHI_12" to R.string.tithi_12,
        "TITHI_13" to R.string.tithi_13,
        "TITHI_14" to R.string.tithi_14,
        "TITHI_15" to R.string.tithi_15,
        "TITHI_16" to R.string.tithi_16,
        "TITHI_17" to R.string.tithi_17,
        "TITHI_18" to R.string.tithi_18,
        "TITHI_19" to R.string.tithi_19,
        "TITHI_20" to R.string.tithi_20,
        "TITHI_21" to R.string.tithi_21,
        "TITHI_22" to R.string.tithi_22,
        "TITHI_23" to R.string.tithi_23,
        "TITHI_24" to R.string.tithi_24,
        "TITHI_25" to R.string.tithi_25,
        "TITHI_26" to R.string.tithi_26,
        "TITHI_27" to R.string.tithi_27,
        "TITHI_28" to R.string.tithi_28,
        "TITHI_29" to R.string.tithi_29,
        "TITHI_30" to R.string.tithi_30
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_tithi_options)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tithiOptions) { (id, resId) ->
                SettingsToggleItem(
                    label = stringResource(resId),
                    checked = enabledTithis.contains(id),
                    onCheckedChange = { viewModel.updateEnabledTithi(id, it) }
                )
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}
