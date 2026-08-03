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
fun NakshatraSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val enabledStars by viewModel.enabledNakshatras.collectAsState()

    val starOptions = (1..27).map { i ->
        "STAR_$i" to when (i) {
            1 -> R.string.star_1
            2 -> R.string.star_2
            3 -> R.string.star_3
            4 -> R.string.star_4
            5 -> R.string.star_5
            6 -> R.string.star_6
            7 -> R.string.star_7
            8 -> R.string.star_8
            9 -> R.string.star_9
            10 -> R.string.star_10
            11 -> R.string.star_11
            12 -> R.string.star_12
            13 -> R.string.star_13
            14 -> R.string.star_14
            15 -> R.string.star_15
            16 -> R.string.star_16
            17 -> R.string.star_17
            18 -> R.string.star_18
            19 -> R.string.star_19
            20 -> R.string.star_20
            21 -> R.string.star_21
            22 -> R.string.star_22
            23 -> R.string.star_23
            24 -> R.string.star_24
            25 -> R.string.star_25
            26 -> R.string.star_26
            27 -> R.string.star_27
            else -> R.string.star_1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_star_options)) },
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
            items(starOptions) { (id, resId) ->
                SettingsToggleItem(
                    label = stringResource(resId),
                    checked = enabledStars.contains(id),
                    onCheckedChange = { viewModel.updateEnabledNakshatra(id, it) }
                )
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}
