package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.Timing
import com.suresh.sacredtimeline.ui.dashboard.TimelineUiState
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PortraitTimelineLayout(
    uiState: TimelineUiState,
    selectedDate: LocalDate,
    dates: List<LocalDate>,
    pagerState: PagerState,
    viewMode: ViewMode,
    timelineScale: Float,
    pinchToZoomEnabled: Boolean,
    is24Hour: Boolean,
    showNowLine: Boolean,
    nowLineColor: Int,
    nowLineThickness: Float,
    columnVisibility: Set<String>,
    columnOrder: List<String>,
    showTamilDate: Boolean,
    showTamilYear: Boolean,
    showPirai: Boolean,
    onMenuClick: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onScaleChange: (Float) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDateClick: () -> Unit,
    onLocationClick: (String) -> Unit,
    onTimingClick: (Timing) -> Unit,
    onTodayClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.label_menu))
                    }
                },
                title = { 
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.clickable { onDateClick() }
                            )
                            if (uiState is TimelineUiState.Success) {
                                Text(
                                    text = " • ",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                )
                                val displayLocation = if (uiState.locationName == "Unknown Location") stringResource(R.string.label_unknown_location) else uiState.locationName
                                Text(
                                    text = "${if (uiState.isLocationAuto) "📍" else "✎"} $displayLocation",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.clickable { onLocationClick(uiState.locationName) }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onTodayClick) {
                        Icon(Icons.Filled.Today, contentDescription = stringResource(R.string.label_today))
                    }
                    IconButton(onClick = onCalendarClick) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.label_select_date))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            HorizontalDateDial(
                selectedDate = selectedDate,
                dates = dates,
                onDateSelected = onDateSelected
            )
            
            Box(modifier = Modifier.weight(1f)) {
                TimelinePager(
                    uiState = uiState,
                    dates = dates,
                    pagerState = pagerState,
                    viewMode = viewMode,
                    timelineScale = timelineScale,
                    pinchToZoomEnabled = pinchToZoomEnabled,
                    is24Hour = is24Hour,
                    showNowLine = showNowLine,
                    nowLineColor = nowLineColor,
                    nowLineThickness = nowLineThickness,
                    columnVisibility = columnVisibility,
                    columnOrder = columnOrder,
                    showTamilDate = showTamilDate,
                    showTamilYear = showTamilYear,
                    showPirai = showPirai,
                    onScaleChange = onScaleChange,
                    onZoomIn = onZoomIn,
                    onZoomOut = onZoomOut,
                    onTimingClick = onTimingClick,
                    isLandscape = false
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LandscapeTimelineLayout(
    uiState: TimelineUiState,
    selectedDate: LocalDate,
    dates: List<LocalDate>,
    pagerState: PagerState,
    viewMode: ViewMode,
    timelineScale: Float,
    pinchToZoomEnabled: Boolean,
    is24Hour: Boolean,
    showNowLine: Boolean,
    nowLineColor: Int,
    nowLineThickness: Float,
    columnVisibility: Set<String>,
    columnOrder: List<String>,
    showTamilDate: Boolean,
    showTamilYear: Boolean,
    showPirai: Boolean,
    onMenuClick: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onScaleChange: (Float) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDateClick: () -> Unit,
    onLocationClick: (String) -> Unit,
    onTimingClick: (Timing) -> Unit,
    onTodayClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Side Panel
            Surface(
                modifier = Modifier
                    .width(220.dp)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (uiState is TimelineUiState.Success) {
                                val displayLocation = if (uiState.locationName == "Unknown Location") stringResource(R.string.label_unknown_location) else uiState.locationName
                                Text(
                                    text = "${if (uiState.isLocationAuto) "📍" else "✎"} $displayLocation",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.clickable { onLocationClick(uiState.locationName) }
                                )
                            }
                        }
                        Row {
                            IconButton(onClick = onMenuClick, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.label_menu), modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = onTodayClick, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.Today, contentDescription = stringResource(R.string.label_today), modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = onCalendarClick, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.label_select_date), modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.clickable { onDateClick() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState is TimelineUiState.Success) {
                        val dayData = uiState.days[selectedDate]
                        if (dayData != null) {
                            SunTimesDisplay(
                                sunrise = dayData.sunrise,
                                sunset = dayData.sunset,
                                tamilDay = dayData.tamilDay,
                                tamilMonthResId = dayData.tamilMonthResId,
                                tamilYearResId = dayData.tamilYearResId,
                                pakshaResId = dayData.pakshaResId,
                                pakshaDay = dayData.pakshaDay,
                                tithiResId = dayData.tithiResId,
                                nakshatraResId = dayData.nakshatraResId,
                                tithiValue = dayData.tithiValue,
                                specialEvents = dayData.specialEvents,
                                isSubhaMuhurtham = dayData.isSubhaMuhurtham,
                                abhijitMuhurtham = dayData.abhijitMuhurtham,
                                showTamilDate = showTamilDate,
                                showTamilYear = showTamilYear,
                                showPirai = showPirai,
                                isFallback = dayData.isFallback,
                                isLandscape = true,
                                is24Hour = is24Hour
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.label_select_date).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    VerticalDateDial(
                        selectedDate = selectedDate,
                        dates = dates,
                        onDateSelected = onDateSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Right Main Content
            Column(modifier = Modifier.weight(1f)) {
                TimelinePager(
                    uiState = uiState,
                    dates = dates,
                    pagerState = pagerState,
                    viewMode = viewMode,
                    timelineScale = timelineScale,
                    pinchToZoomEnabled = pinchToZoomEnabled,
                    is24Hour = is24Hour,
                    showNowLine = showNowLine,
                    nowLineColor = nowLineColor,
                    nowLineThickness = nowLineThickness,
                    columnVisibility = columnVisibility,
                    columnOrder = columnOrder,
                    showTamilDate = showTamilDate,
                    showTamilYear = showTamilYear,
                    showPirai = showPirai,
                    onScaleChange = onScaleChange,
                    onZoomIn = onZoomIn,
                    onZoomOut = onZoomOut,
                    onTimingClick = onTimingClick,
                    isLandscape = true
                )
            }
        }
    }
}
