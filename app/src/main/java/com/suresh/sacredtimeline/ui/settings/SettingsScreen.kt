package com.suresh.sacredtimeline.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                .padding(padding),
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
                SettingsSection(title = "Column Management") {
                    Text(
                        "Drag-and-drop or use arrows to reorder columns in Composite view. Toggle visibility to hide/show.",
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
                    SettingsToggleItem(
                        label = "Show NOW Line",
                        checked = showNowLine,
                        onCheckedChange = { viewModel.setShowNowLine(it) }
                    )
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
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text("${String.format(Locale.US, "%.1f", value)}x", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.2f..3.0f,
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
