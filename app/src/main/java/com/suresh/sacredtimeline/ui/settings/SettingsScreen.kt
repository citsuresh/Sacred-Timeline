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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToTimelineDisplaySettings: () -> Unit,
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
    val language by viewModel.language.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val timelineViewStyle by viewModel.timelineViewStyle.collectAsState()
    
    @Composable
    fun getLocalizedViewModeName(mode: ViewMode): String = when (mode) {
        ViewMode.COMPOSITE -> stringResource(R.string.view_mode_composite)
        ViewMode.UNIVERSAL -> stringResource(R.string.view_mode_universal)
        ViewMode.NERAM_MUHURTHAM -> stringResource(R.string.view_mode_neram_muhurtham)
        ViewMode.NERAM -> stringResource(R.string.view_mode_neram)
        ViewMode.BRAHMA -> stringResource(R.string.muhurtham_brahma)
        ViewMode.ABHIJIT -> stringResource(R.string.muhurtham_abhijit)
        ViewMode.GOWRI -> stringResource(R.string.view_mode_gowri)
        ViewMode.HORA -> stringResource(R.string.nav_hora)
        ViewMode.MAITRA -> stringResource(R.string.timing_maitra)
    }

    val englishLabel = "English"
    val tamilLabel = "தமிழ்"

    val currentLanguageLabel = remember(language) {
        if (language.startsWith("ta")) tamilLabel else englishLabel
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_cancel))
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
            // 1. General Settings
            item {
                SettingsSection(title = stringResource(R.string.settings_general)) {
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_language),
                        selected = currentLanguageLabel,
                        options = listOf(englishLabel, tamilLabel),
                        onOptionSelected = {
                            viewModel.setLanguage(if (it == englishLabel) "en" else "ta")
                        }
                    )

                    val themeSystem = stringResource(R.string.settings_theme_system)
                    val themeLight = stringResource(R.string.settings_theme_light)
                    val themeDark = stringResource(R.string.settings_theme_dark)
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_theme),
                        selected = when (themeMode) {
                            "LIGHT" -> themeLight
                            "DARK" -> themeDark
                            else -> themeSystem
                        },
                        options = listOf(themeSystem, themeLight, themeDark),
                        onOptionSelected = {
                            val mode = when (it) {
                                themeLight -> "LIGHT"
                                themeDark -> "DARK"
                                else -> "SYSTEM"
                            }
                            viewModel.setThemeMode(mode)
                        }
                    )

                    val viewModeOptions = ViewMode.entries
                    val viewModeLabels = viewModeOptions.map { getLocalizedViewModeName(it) }
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_default_launch_view),
                        selected = getLocalizedViewModeName(defaultLaunchView),
                        options = viewModeLabels,
                        onOptionSelected = { label ->
                            val index = viewModeLabels.indexOf(label)
                            if (index != -1) viewModel.setDefaultLaunchView(viewModeOptions[index])
                        }
                    )

                    SettingsToggleItem(
                        label = stringResource(R.string.settings_time_format_24h),
                        checked = timeFormat24h,
                        onCheckedChange = { viewModel.setTimeFormat24h(it) }
                    )
                }
            }

            // 2. Traditional Conventions
            item {
                SettingsSection(title = stringResource(R.string.settings_conventions)) {
                    val lunarMonthSystem by viewModel.lunarMonthSystem.collectAsState()
                    val amantaLabel = stringResource(R.string.settings_amanta)
                    val purnimantaLabel = stringResource(R.string.settings_purnimanta)
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_lunar_month_system),
                        selected = if (lunarMonthSystem == "AMANTA") amantaLabel else purnimantaLabel,
                        options = listOf(amantaLabel, purnimantaLabel),
                        onOptionSelected = {
                            viewModel.setLunarMonthSystem(if (it == amantaLabel) "AMANTA" else "PURNIMANTA")
                        }
                    )

                    val sunriseDefinition by viewModel.sunriseDefinition.collectAsState()
                    val scientificLabel = stringResource(R.string.settings_sunrise_scientific)
                    val traditionalLabel = stringResource(R.string.settings_sunrise_traditional)
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_sunrise_definition),
                        selected = if (sunriseDefinition == "SCIENTIFIC") scientificLabel else traditionalLabel,
                        options = listOf(scientificLabel, traditionalLabel),
                        onOptionSelected = {
                            viewModel.setSunriseDefinition(if (it == scientificLabel) "SCIENTIFIC" else "TRADITIONAL")
                        }
                    )

                    val specialPeriodStyle by viewModel.specialPeriodStyle.collectAsState()
                    val proportionalLabel = stringResource(R.string.settings_style_proportional)
                    val fixedLabel = stringResource(R.string.settings_style_fixed)
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_special_period_style),
                        selected = if (specialPeriodStyle == "PROPORTIONAL") proportionalLabel else fixedLabel,
                        options = listOf(proportionalLabel, fixedLabel),
                        onOptionSelected = {
                            viewModel.setSpecialPeriodStyle(if (it == proportionalLabel) "PROPORTIONAL" else "FIXED")
                        }
                    )
                }
            }

            // 3. Timeline Layout
            item {
                SettingsSection(title = stringResource(R.string.settings_timeline_layout)) {
                    val fixedTrackLabel = stringResource(R.string.settings_style_fixed_track)
                    val equalRectLabel = stringResource(R.string.settings_style_equal_rectangular)
                    val orthogonalSteppedLabel = stringResource(R.string.settings_style_orthogonal_stepped)
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_timeline_style),
                        selected = when (timelineViewStyle) {
                            "FIXED_3_TRACK" -> fixedTrackLabel
                            "ORTHOGONAL_STEPPED" -> orthogonalSteppedLabel
                            else -> equalRectLabel
                        },
                        options = listOf(fixedTrackLabel, equalRectLabel, orthogonalSteppedLabel),
                        onOptionSelected = {
                            val style = when (it) {
                                fixedTrackLabel -> "FIXED_3_TRACK"
                                orthogonalSteppedLabel -> "ORTHOGONAL_STEPPED"
                                else -> "EQUAL_DISTRIBUTION"
                            }
                            viewModel.setTimelineViewStyle(style)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.settings_columns_composite),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        stringResource(R.string.settings_columns_desc),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    columnOrder.forEachIndexed { index, colId ->
                        val isVisible = columnVisibility.contains(colId)
                        ColumnOrderItem(
                            name = when(colId) {
                                "UNIVERSAL" -> stringResource(R.string.nav_universal)
                                "NERAM_MUHURTHAM" -> stringResource(R.string.nav_neram_muhurtham)
                                "NERAM" -> stringResource(R.string.nav_nalla_neram)
                                "BRAHMA" -> stringResource(R.string.muhurtham_brahma)
                                "ABHIJIT" -> stringResource(R.string.muhurtham_abhijit)
                                "MAITRA" -> stringResource(R.string.timing_maitra)
                                "GOWRI" -> stringResource(R.string.view_mode_gowri)
                                "HORA" -> stringResource(R.string.nav_hora)
                                else -> colId
                            },
                            isVisible = isVisible,
                            onToggleVisibility = { viewModel.updateColumnVisibility(colId, it) },
                            onMoveUp = if (index > 0) { { viewModel.moveColumn(colId, -1) } } else null,
                            onMoveDown = if (index < columnOrder.size - 1) { { viewModel.moveColumn(colId, 1) } } else null
                        )
                        if (index < columnOrder.size - 1) HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsSliderItem(
                        label = stringResource(R.string.settings_zoom_composite),
                        value = compositeScale,
                        onValueChange = { viewModel.updateCompositeScale(it) }
                    )
                    SettingsSliderItem(
                        label = stringResource(R.string.settings_zoom_single),
                        value = singleViewScale,
                        onValueChange = { viewModel.updateSingleViewScale(it) }
                    )
                    SettingsToggleItem(
                        label = stringResource(R.string.settings_pinch_zoom),
                        checked = pinchToZoomEnabled,
                        onCheckedChange = { viewModel.setPinchToZoomEnabled(it) }
                    )
                }
            }

            // 4. Display & Indicators
            item {
                SettingsSection(title = stringResource(R.string.settings_display_indicators)) {
                    val nowLineColor by viewModel.nowLineColor.collectAsState()
                    val nowLineThickness by viewModel.nowLineThickness.collectAsState()
                    SettingsToggleItem(
                        label = stringResource(R.string.settings_show_now_line),
                        checked = showNowLine,
                        onCheckedChange = { viewModel.setShowNowLine(it) }
                    )
                    if (showNowLine) {
                        Text(stringResource(R.string.settings_line_color), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            val colors = listOf(Color.Red, Color(0xFFFF5722), Color(0xFFFFC107), Color.White, Color(0xFF4CAF50), Color(0xFF2196F3))
                            colors.forEach { color ->
                                val isSelected = nowLineColor == color.toArgb()
                                Box(
                                    modifier = Modifier.size(32.dp).clip(androidx.compose.foundation.shape.CircleShape).background(color).border(
                                        width = if (isSelected) 3.dp else 1.dp, color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f), shape = androidx.compose.foundation.shape.CircleShape
                                    ).clickable { viewModel.setNowLineColor(color.toArgb()) }
                                )
                            }
                        }
                        SettingsSliderItem(
                            label = stringResource(R.string.settings_line_thickness),
                            value = nowLineThickness,
                            onValueChange = { viewModel.setNowLineThickness(it) },
                            valueRange = 1f..10f
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                    
                    TextButton(onClick = onNavigateToTimelineDisplaySettings, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = stringResource(R.string.settings_indicators), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // 5. Location
            item {
                SettingsSection(title = stringResource(R.string.settings_location)) {
                    val locationMode by viewModel.locationMode.collectAsState()
                    val manualCityName by viewModel.manualCityName.collectAsState()
                    val searchState by viewModel.searchState.collectAsState()
                    val autoLabel = stringResource(R.string.settings_location_auto)
                    val manualLabel = stringResource(R.string.settings_location_manual)
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_location_mode),
                        selected = if (locationMode == "AUTO") autoLabel else manualLabel,
                        options = listOf(autoLabel, manualLabel),
                        onOptionSelected = { viewModel.setLocationMode(if (it == autoLabel) "AUTO" else "MANUAL") }
                    )
                    if (locationMode == "MANUAL") {
                        var localInput by remember { mutableStateOf(manualCityName) }
                        LaunchedEffect(manualCityName) { if (localInput != manualCityName) localInput = manualCityName }
                        OutlinedTextField(
                            value = localInput, onValueChange = { localInput = it }, label = { Text(stringResource(R.string.settings_city_name)) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), singleLine = true,
                            trailingIcon = {
                                if (searchState == SearchState.Searching) CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                else IconButton(onClick = { viewModel.setManualCityName(localInput); viewModel.searchCity(localInput) }) {
                                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.settings_search_city))
                                }
                            },
                            isError = searchState is SearchState.Error
                        )
                        if (searchState is SearchState.Error) Text(text = (searchState as SearchState.Error).message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))
                        if (searchState is SearchState.Results) {
                            val results = (searchState as SearchState.Results).addresses
                            Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 8.dp, shadowElevation = 4.dp, shape = MaterialTheme.shapes.small) {
                                Column {
                                    results.forEach { address ->
                                        DropdownMenuItem(
                                            text = { Column { Text(address.locality ?: address.featureName ?: "Unknown"); Text("${address.adminArea ?: ""}, ${address.countryName ?: ""}", style = MaterialTheme.typography.labelSmall) } },
                                            onClick = { viewModel.selectCity(address) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. Home Screen Widget
            item {
                SettingsSection(title = stringResource(R.string.settings_widget_mgmt)) {
                    val refreshMinutes by viewModel.widgetRefreshMinutes.collectAsState()
                    val refreshOptions = listOf(15, 30, 60)
                    val refreshLabels = refreshOptions.map { if (it < 60) stringResource(R.string.settings_refresh_min, it) else stringResource(R.string.settings_refresh_hr, it / 60) }
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_widget_refresh),
                        selected = if (refreshMinutes < 60) stringResource(R.string.settings_refresh_min, refreshMinutes) else stringResource(R.string.settings_refresh_hr, refreshMinutes / 60),
                        options = refreshLabels, onOptionSelected = { label -> val index = refreshLabels.indexOf(label); if (index != -1) viewModel.setWidgetRefreshMinutes(refreshOptions[index]) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                    Text(stringResource(R.string.settings_widget_columns), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
                    widgetColumnOrder.forEachIndexed { index, colId ->
                        val isVisible = widgetColumnVisibility.contains(colId)
                        ColumnOrderItem(
                            name = when(colId) {
                                "UNIVERSAL" -> stringResource(R.string.nav_universal)
                                "NERAM_MUHURTHAM" -> stringResource(R.string.nav_neram_muhurtham)
                                "NERAM" -> stringResource(R.string.nav_nalla_neram)
                                "BRAHMA" -> stringResource(R.string.muhurtham_brahma)
                                "ABHIJIT" -> stringResource(R.string.muhurtham_abhijit)
                                "MAITRA" -> stringResource(R.string.timing_maitra)
                                "GOWRI" -> stringResource(R.string.view_mode_gowri)
                                "HORA" -> stringResource(R.string.nav_hora)
                                else -> colId
                            },
                            isVisible = isVisible, onToggleVisibility = { viewModel.updateWidgetColumnVisibility(colId, it) },
                            onMoveUp = if (index > 0) { { viewModel.moveWidgetColumn(colId, -1) } } else null,
                            onMoveDown = if (index < widgetColumnOrder.size - 1) { { viewModel.moveWidgetColumn(colId, 1) } } else null
                        )
                        if (index < widgetColumnOrder.size - 1) HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                    }
                }
            }

            // 7. System & Advanced
            item {
                SettingsSection(title = stringResource(R.string.settings_advanced)) {
                    val preloadDays by viewModel.preloadDays.collectAsState()
                    val preloadOptions = listOf(3, 5, 7)
                    val preloadLabels = preloadOptions.map { stringResource(R.string.settings_preload_days, it) }
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_preload_range),
                        selected = stringResource(R.string.settings_preload_days, preloadDays),
                        options = preloadLabels, onOptionSelected = { label -> val index = preloadLabels.indexOf(label); if (index != -1) viewModel.setPreloadDays(preloadOptions[index]) }
                    )
                    Button(onClick = { viewModel.clearCache() }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Text(stringResource(R.string.settings_clear_cache))
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
                contentDescription = stringResource(R.string.label_toggle_visibility),
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
            Icon(Icons.Default.ArrowUpward, contentDescription = stringResource(R.string.label_move_up), modifier = Modifier.size(20.dp))
        }

        IconButton(onClick = onMoveDown ?: {}, enabled = onMoveDown != null) {
            Icon(Icons.Default.ArrowDownward, contentDescription = stringResource(R.string.label_move_down), modifier = Modifier.size(20.dp))
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
        Text(
            text = label, 
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f).padding(end = 16.dp)
        )
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(modifier = Modifier.align(Alignment.End)) {
            TextButton(
                onClick = { expanded = true },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = selected,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
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
