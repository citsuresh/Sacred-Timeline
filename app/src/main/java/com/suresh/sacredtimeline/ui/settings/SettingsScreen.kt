package com.suresh.sacredtimeline.ui.settings

import android.location.Address
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val compositeScale by viewModel.compositeScale.collectAsState()
    val singleViewScale by viewModel.singleViewScale.collectAsState()
    val defaultLaunchView by viewModel.defaultLaunchView.collectAsState()
    val timeFormat24h by viewModel.timeFormat24h.collectAsState()
    val showNowLine by viewModel.showNowLine.collectAsState()
    val pinchToZoomEnabled by viewModel.pinchToZoomEnabled.collectAsState()
    val columnVisibility by viewModel.columnVisibility.collectAsState()
    val columnOrder by viewModel.columnOrder.collectAsState()
    val widgetColumnVisibility by viewModel.widgetColumnVisibility.collectAsState()
    val widgetColumnOrder by viewModel.widgetColumnOrder.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SettingsSection(title = "Display") {
                    SettingsDropdownItem(
                        label = "Default View on Launch",
                        selected = defaultLaunchView.name,
                        options = ViewMode.entries.map { it.name },
                        onOptionSelected = { viewModel.setDefaultLaunchView(ViewMode.valueOf(it)) }
                    )
                    SettingsToggleItem(
                        label = "Use 24-Hour Format",
                        checked = timeFormat24h,
                        onCheckedChange = { viewModel.setTimeFormat24h(it) }
                    )
                }
            }

            item {
                SettingsSection(title = "Column Management (Composite View Only)") {
                    Text(
                        "Reorder or hide columns for the Composite View. Single views (Hora, Gowri, etc.) always show their respective column.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    columnOrder.forEachIndexed { index, colId ->
                        val isVisible = columnVisibility.contains(colId)
                        ColumnOrderItem(
                            name = colId,
                            isVisible = isVisible,
                            onToggleVisibility = { viewModel.updateColumnVisibility(colId, it) },
                            onMoveUp = if (index > 0) { { viewModel.moveColumn(colId, -1) } } else null,
                            onMoveDown = if (index < columnOrder.size - 1) { { viewModel.moveColumn(colId, 1) } } else null
                        )
                        if (index < columnOrder.size - 1) HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                    }
                }
            }

            item {
                SettingsSection(title = "Location") {
                    val locationMode by viewModel.locationMode.collectAsState()
                    val manualCityName by viewModel.manualCityName.collectAsState()
                    val searchState by viewModel.searchState.collectAsState()

                    SettingsDropdownItem(
                        label = "Location Mode",
                        selected = if (locationMode == "AUTO") "GPS Auto-detect" else "Manual City",
                        options = listOf("GPS Auto-detect", "Manual City"),
                        onOptionSelected = {
                            viewModel.setLocationMode(if (it == "GPS Auto-detect") "AUTO" else "MANUAL")
                        }
                    )
                    
                    if (locationMode == "MANUAL") {
                        var localInput by remember { mutableStateOf(manualCityName) }
                        
                        LaunchedEffect(manualCityName) {
                            if (localInput != manualCityName) {
                                localInput = manualCityName
                            }
                        }

                        OutlinedTextField(
                            value = localInput,
                            onValueChange = { localInput = it },
                            label = { Text("City Name") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            singleLine = true,
                            trailingIcon = {
                                if (searchState == SearchState.Searching) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                } else {
                                    IconButton(onClick = { 
                                        viewModel.setManualCityName(localInput)
                                        viewModel.searchCity(localInput) 
                                    }) {
                                        Icon(Icons.Default.Search, contentDescription = "Search City")
                                    }
                                }
                            },
                            isError = searchState is SearchState.Error
                        )
                        
                        if (searchState is SearchState.Error) {
                            Text(
                                text = (searchState as SearchState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        if (searchState is SearchState.Results) {
                            val results = (searchState as SearchState.Results).addresses
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                tonalElevation = 8.dp,
                                shadowElevation = 4.dp,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Column {
                                    results.forEach { address ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(address.locality ?: address.featureName ?: "Unknown")
                                                    Text(
                                                        "${address.adminArea ?: ""}, ${address.countryName ?: ""}",
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                            },
                                            onClick = { viewModel.selectCity(address) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Widget Management") {
                    val refreshMinutes by viewModel.widgetRefreshMinutes.collectAsState()
                    SettingsDropdownItem(
                        label = "Widget Refresh Duration",
                        selected = when (refreshMinutes) {
                            15 -> "15 min"
                            30 -> "30 min"
                            60 -> "1 hr"
                            else -> "$refreshMinutes min"
                        },
                        options = listOf("15 min", "30 min", "1 hr"),
                        onOptionSelected = {
                            val mins = when (it) {
                                "15 min" -> 15
                                "30 min" -> 30
                                "1 hr" -> 60
                                else -> 30
                            }
                            viewModel.setWidgetRefreshMinutes(mins)
                        }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                    
                    Text(
                        "Column Management (Widget)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    widgetColumnOrder.forEachIndexed { index, colId ->
                        val isVisible = widgetColumnVisibility.contains(colId)
                        ColumnOrderItem(
                            name = colId,
                            isVisible = isVisible,
                            onToggleVisibility = { viewModel.updateWidgetColumnVisibility(colId, it) },
                            onMoveUp = if (index > 0) { { viewModel.moveWidgetColumn(colId, -1) } } else null,
                            onMoveDown = if (index < widgetColumnOrder.size - 1) { { viewModel.moveWidgetColumn(colId, 1) } } else null
                        )
                        if (index < widgetColumnOrder.size - 1) HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                    }
                }
            }

            item {
                SettingsSection(title = "Timeline Density") {
                    SettingsSliderItem(
                        label = "Composite View Zoom",
                        value = compositeScale,
                        onValueChange = { viewModel.updateCompositeScale(it) }
                    )
                    SettingsSliderItem(
                        label = "Single View Zoom",
                        value = singleViewScale,
                        onValueChange = { viewModel.updateSingleViewScale(it) }
                    )
                    SettingsToggleItem(
                        label = "Enable Pinch-to-Zoom",
                        checked = pinchToZoomEnabled,
                        onCheckedChange = { viewModel.setPinchToZoomEnabled(it) }
                    )
                }
            }

            item {
                SettingsSection(title = "Timeline Indicators") {
                    val nowLineColor by viewModel.nowLineColor.collectAsState()
                    val nowLineThickness by viewModel.nowLineThickness.collectAsState()

                    SettingsToggleItem(
                        label = "Show NOW Line",
                        checked = showNowLine,
                        onCheckedChange = { viewModel.setShowNowLine(it) }
                    )
                    
                    if (showNowLine) {
                        Text(
                            "Line Color",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val colors = listOf(
                                Color.Red, 
                                Color(0xFFFF5722), // Saffron/Orange
                                Color(0xFFFFC107), // Gold
                                Color.White,
                                Color(0xFF4CAF50), // Green
                                Color(0xFF2196F3)  // Blue
                            )
                            colors.forEach { color ->
                                val isSelected = nowLineColor == color.toArgb()
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                        .clickable { viewModel.setNowLineColor(color.toArgb()) }
                                )
                            }
                        }

                        SettingsSliderItem(
                            label = "Line Thickness",
                            value = nowLineThickness,
                            onValueChange = { viewModel.setNowLineThickness(it) },
                            valueRange = 1f..10f
                        )
                    }
                }
            }

            item {
                SettingsSection(title = "Advanced (Cache)") {
                    val preloadDays by viewModel.preloadDays.collectAsState()
                    SettingsDropdownItem(
                        label = "Preload Range",
                        selected = "$preloadDays days",
                        options = listOf("3 days", "5 days", "7 days"),
                        onOptionSelected = {
                            viewModel.setPreloadDays(it.split(" ")[0].toInt())
                        }
                    )
                    Button(
                        onClick = { viewModel.clearCache() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Clear Cache")
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnOrderItem(
    name: String,
    isVisible: Boolean,
    onToggleVisibility: (Boolean) -> Unit,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onToggleVisibility(!isVisible) }) {
            Icon(
                imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle Visibility",
                tint = if (isVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }
        
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = if (isVisible) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        IconButton(onClick = onMoveUp ?: {}, enabled = onMoveUp != null) {
            Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", modifier = Modifier.size(20.dp))
        }

        IconButton(onClick = onMoveDown ?: {}, enabled = onMoveDown != null) {
            Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title.uppercase(Locale.US),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsToggleItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsSliderItem(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0.2f..3.0f
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text("${String.format(Locale.US, "%.1f", value)}${if (valueRange.start == 0.2f) "x" else "dp"}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SettingsDropdownItem(
    label: String,
    selected: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Box {
            TextButton(onClick = { expanded = true }) {
                Text(selected)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
