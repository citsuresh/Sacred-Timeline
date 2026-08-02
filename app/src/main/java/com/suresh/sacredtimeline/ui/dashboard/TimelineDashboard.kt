package com.suresh.sacredtimeline.ui.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.Timing
import com.suresh.sacredtimeline.ui.dashboard.components.*
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import com.suresh.sacredtimeline.ui.theme.SacredTimelineTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@Composable
fun TimelineDashboard(
    modifier: Modifier = Modifier,
    viewModel: TimelineViewModel = viewModel(),
    viewMode: ViewMode = ViewMode.COMPOSITE,
    onMenuClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val timelineScale by viewModel.timelineScale.collectAsState()
    val pinchToZoomEnabled by viewModel.pinchToZoomEnabled.collectAsState(initial = true)
    val timeFormat24h by viewModel.timeFormat24h.collectAsState(initial = false)
    val showNowLine by viewModel.showNowLine.collectAsState()
    val nowLineColor by viewModel.nowLineColor.collectAsState()
    val nowLineThickness by viewModel.nowLineThickness.collectAsState()
    val columnVisibility by viewModel.columnVisibility.collectAsState(initial = setOf("NERAM", "GOWRI", "HORA"))
    val columnOrder by viewModel.columnOrder.collectAsState(initial = listOf("NERAM", "GOWRI", "HORA"))
    
    LaunchedEffect(viewMode) {
        viewModel.setViewMode(viewMode)
    }

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            viewModel.onLocationPermissionGranted()
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var manualLocationName by remember { mutableStateOf("") }
    
    var selectedTimingForDetail by remember { mutableStateOf<Timing?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet && selectedTimingForDetail != null) {
        TimingDetailSheet(
            timing = selectedTimingForDetail!!,
            sheetState = sheetState,
            onDismiss = { showSheet = false },
            viewModel = viewModel
        )
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        viewModel.updateDate(newDate)
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.btn_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text(stringResource(R.string.dialog_change_location)) },
            text = {
                Column {
                    Text(stringResource(R.string.dialog_location_hint), style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = manualLocationName,
                        onValueChange = { manualLocationName = it },
                        label = { Text(stringResource(R.string.settings_city_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (manualLocationName.isNotBlank()) {
                        viewModel.updateManualLocation(manualLocationName)
                    }
                    showLocationDialog = false
                }) {
                    Text(stringResource(R.string.btn_update))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    val dates = remember {
        (-30..30).map { LocalDate.now().plusDays(it.toLong()) }
    }
    val initialPage = dates.indexOf(selectedDate).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { dates.size })

    LaunchedEffect(selectedDate) {
        val page = dates.indexOf(selectedDate)
        if (page != -1 && page != pagerState.currentPage) {
            pagerState.animateScrollToPage(page)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val date = dates[pagerState.currentPage]
        if (date != selectedDate) {
            viewModel.updateDate(date)
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val onZoomIn = { viewModel.updateTimelineScale(timelineScale + 0.1f) }
    val onZoomOut = { viewModel.updateTimelineScale(timelineScale - 0.1f) }
    val onScaleChange = { scale: Float -> viewModel.updateTimelineScale(scale) }

    if (isLandscape) {
        LandscapeTimelineLayout(
            uiState = uiState,
            selectedDate = selectedDate,
            dates = dates,
            pagerState = pagerState,
            viewMode = viewMode,
            timelineScale = timelineScale,
            pinchToZoomEnabled = pinchToZoomEnabled,
            is24Hour = timeFormat24h,
            showNowLine = showNowLine,
            nowLineColor = nowLineColor,
            nowLineThickness = nowLineThickness,
            columnVisibility = columnVisibility,
            columnOrder = columnOrder,
            onMenuClick = onMenuClick,
            onZoomIn = onZoomIn,
            onZoomOut = onZoomOut,
            onScaleChange = onScaleChange,
            onDateSelected = { viewModel.updateDate(it) },
            onDateClick = { showDatePicker = true },
            onLocationClick = { name ->
                manualLocationName = name
                showLocationDialog = true
            },
            onTimingClick = { timing ->
                selectedTimingForDetail = timing
                showSheet = true
            },
            onTodayClick = { viewModel.updateDate(LocalDate.now()) },
            onCalendarClick = { showDatePicker = true }
        )
    } else {
        PortraitTimelineLayout(
            uiState = uiState,
            selectedDate = selectedDate,
            dates = dates,
            pagerState = pagerState,
            viewMode = viewMode,
            timelineScale = timelineScale,
            pinchToZoomEnabled = pinchToZoomEnabled,
            is24Hour = timeFormat24h,
            showNowLine = showNowLine,
            nowLineColor = nowLineColor,
            nowLineThickness = nowLineThickness,
            columnVisibility = columnVisibility,
            columnOrder = columnOrder,
            onMenuClick = onMenuClick,
            onZoomIn = onZoomIn,
            onZoomOut = onZoomOut,
            onScaleChange = onScaleChange,
            onDateSelected = { viewModel.updateDate(it) },
            onDateClick = { showDatePicker = true },
            onLocationClick = { name ->
                manualLocationName = name
                showLocationDialog = true
            },
            onTimingClick = { timing ->
                selectedTimingForDetail = timing
                showSheet = true
            },
            onTodayClick = { viewModel.updateDate(LocalDate.now()) },
            onCalendarClick = { showDatePicker = true }
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun TimelineDashboardPreview() {
    SacredTimelineTheme {
        TimelineDashboard()
    }
}
