package com.suresh.sacredtimeline.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suresh.sacredtimeline.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineDisplaySettingsScreen(
    onBack: () -> Unit,
    onNavigateToTithiSettings: () -> Unit,
    onNavigateToNakshatraSettings: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val showTamilDate by viewModel.showTamilDate.collectAsState()
    val showTamilYear by viewModel.showTamilYear.collectAsState()
    val showPirai by viewModel.showPirai.collectAsState()
    val showSunrise by viewModel.showSunrise.collectAsState()
    val showSunset by viewModel.showSunset.collectAsState()
    val showBrahmaMuhurtham by viewModel.showBrahmaMuhurtham.collectAsState()
    val showAbhijitMuhurtham by viewModel.showAbhijitMuhurtham.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_indicators)) },
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
            item {
                SettingsToggleItem(
                    label = stringResource(R.string.settings_show_tamil_date),
                    checked = showTamilDate,
                    onCheckedChange = { viewModel.setShowTamilDate(it) }
                )
            }
            item {
                SettingsToggleItem(
                    label = stringResource(R.string.settings_show_tamil_year),
                    checked = showTamilYear,
                    onCheckedChange = { viewModel.setShowTamilYear(it) }
                )
            }
            item {
                SettingsToggleItem(
                    label = stringResource(R.string.settings_show_pirai),
                    checked = showPirai,
                    onCheckedChange = { viewModel.setShowPirai(it) }
                )
            }
            item {
                SettingsToggleItem(
                    label = stringResource(R.string.settings_show_sunrise),
                    checked = showSunrise,
                    onCheckedChange = { viewModel.setShowSunrise(it) }
                )
            }
            item {
                SettingsToggleItem(
                    label = stringResource(R.string.settings_show_sunset),
                    checked = showSunset,
                    onCheckedChange = { viewModel.setShowSunset(it) }
                )
            }
            item {
                SettingsToggleItem(
                    label = stringResource(R.string.settings_show_brahma_muhurtham),
                    checked = showBrahmaMuhurtham,
                    onCheckedChange = { viewModel.setShowBrahmaMuhurtham(it) }
                )
            }
            item {
                SettingsToggleItem(
                    label = stringResource(R.string.settings_show_abhijit_muhurtham),
                    checked = showAbhijitMuhurtham,
                    onCheckedChange = { viewModel.setShowAbhijitMuhurtham(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)
            }

            item {
                TextButton(
                    onClick = onNavigateToTithiSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_tithi_options),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            Icons.Default.ChevronRight, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                TextButton(
                    onClick = onNavigateToNakshatraSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_star_options),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            Icons.Default.ChevronRight, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
