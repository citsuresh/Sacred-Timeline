package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.calculateZoom
import kotlin.math.abs
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.DayData
import com.suresh.sacredtimeline.model.Timing
import com.suresh.sacredtimeline.ui.dashboard.TimelineUiState
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import com.suresh.sacredtimeline.ui.theme.SeparatorGrey
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

private val BASE_HOUR_HEIGHT = 160.dp
private const val START_HOUR = 0
private const val END_HOUR = 24
private val TIME_COLUMN_WIDTH = 65.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimelinePager(
    uiState: TimelineUiState,
    dates: List<LocalDate>,
    pagerState: PagerState,
    onTimingClick: (Timing) -> Unit,
    viewMode: ViewMode,
    timelineScale: Float,
    pinchToZoomEnabled: Boolean,
    is24Hour: Boolean,
    showNowLine: Boolean,
    nowLineColor: Int,
    nowLineThickness: Float,
    columnVisibility: Set<String>,
    columnOrder: List<String>,
    showTamilDate: Boolean = true,
    showTamilYear: Boolean = true,
    showPirai: Boolean = true,
    showSunrise: Boolean = true,
    showSunset: Boolean = true,
    showBrahmaMuhurtham: Boolean = false,
    showAbhijitMuhurtham: Boolean = false,
    onScaleChange: (Float) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onDetailClick: (com.suresh.sacredtimeline.model.DashboardDetail) -> Unit = {},
    isLandscape: Boolean
) {
    val hourHeight = BASE_HOUR_HEIGHT * timelineScale
    
    var showScaleIndicator by remember { mutableStateOf(false) }
    var indicatorTimer by remember { mutableStateOf(0L) }

    LaunchedEffect(timelineScale) {
        showScaleIndicator = true
        indicatorTimer = System.currentTimeMillis()
        delay(1500)
        if (System.currentTimeMillis() - indicatorTimer >= 1500) {
            showScaleIndicator = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(timelineScale, pinchToZoomEnabled) {
                if (pinchToZoomEnabled) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val changes = event.changes
                            if (changes.size >= 2) {
                                val zoom = event.calculateZoom()
                                if (abs(zoom - 1f) > 0.005f) {
                                    val newScale = (timelineScale * zoom).coerceIn(0.2f, 3.0f)
                                    if (newScale != timelineScale) {
                                        onScaleChange(newScale)
                                        changes.forEach { it.consume() }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    ) {
        when (val res = uiState) {
            is TimelineUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is TimelineUiState.Success -> {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val date = dates[page]
                    val dayData = res.days[date]
                    if (dayData != null) {
                        TimelineContent(
                            date = date, 
                            dayData = dayData,
                            onTimingClick = onTimingClick,
                            viewMode = viewMode,
                            hourHeight = hourHeight,
                            onZoomIn = onZoomIn,
                            onZoomOut = onZoomOut,
                            onDetailClick = onDetailClick,
                            isLandscape = isLandscape,
                            is24Hour = is24Hour,
                            showNowLine = showNowLine,
                            nowLineColor = nowLineColor,
                            nowLineThickness = nowLineThickness,
                            columnVisibility = columnVisibility,
                            columnOrder = columnOrder,
                            showTamilDate = showTamilDate,
                            showTamilYear = showTamilYear,
                            showPirai = showPirai,
                            showSunrise = showSunrise,
                            showSunset = showSunset,
                            showBrahmaMuhurtham = showBrahmaMuhurtham,
                            showAbhijitMuhurtham = showAbhijitMuhurtham
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showScaleIndicator,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Text(
                    text = stringResource(R.string.label_zoom_value, timelineScale),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TimelineContent(
    date: LocalDate, 
    dayData: DayData,
    onTimingClick: (Timing) -> Unit,
    viewMode: ViewMode,
    hourHeight: Dp,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onDetailClick: (com.suresh.sacredtimeline.model.DashboardDetail) -> Unit = {},
    isLandscape: Boolean = false,
    is24Hour: Boolean = false,
    showNowLine: Boolean = true,
    nowLineColor: Int = 0xFFFF0000.toInt(),
    nowLineThickness: Float = 2.0f,
    columnVisibility: Set<String> = setOf("NERAM", "GOWRI", "HORA"),
    columnOrder: List<String> = listOf("NERAM", "GOWRI", "HORA"),
    showTamilDate: Boolean = true,
    showTamilYear: Boolean = true,
    showPirai: Boolean = true,
    showSunrise: Boolean = true,
    showSunset: Boolean = true,
    showBrahmaMuhurtham: Boolean = false,
    showAbhijitMuhurtham: Boolean = false
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    var containerHeight by remember { mutableStateOf(0) }
    
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    val isToday = date == LocalDate.now()
    
    LaunchedEffect(isToday) {
        if (isToday) {
            while (true) {
                currentTime = LocalTime.now()
                delay(60000 - (System.currentTimeMillis() % 60000))
            }
        }
    }
    
    LaunchedEffect(containerHeight) {
        if (containerHeight > 0 && isToday) {
            val nowOffset = with(density) { calculateOffset(currentTime, hourHeight).toPx() }
            val centerOffset = containerHeight / 2
            scrollState.scrollTo((nowOffset - centerOffset).toInt().coerceAtLeast(0))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (!isLandscape) {
            SunTimesDisplay(
                sunrise = dayData.sunrise, 
                sunset = dayData.sunset, 
                viewDate = date,
                tamilDay = dayData.tamilDay,
                tamilMonthResId = dayData.tamilMonthResId,
                tamilYearResId = dayData.tamilYearResId,
                pakshaResId = dayData.pakshaResId,
                pakshaDay = dayData.pakshaDay,
                tithis = dayData.tithis,
                nakshatras = dayData.nakshatras,
                tithiValue = dayData.tithis.firstOrNull()?.value ?: 0,
                specialEvents = dayData.specialEvents,
                isSubhaMuhurtham = dayData.isSubhaMuhurtham,
                abhijitMuhurtham = dayData.abhijitMuhurtham,
                brahmaMuhurtham = dayData.brahmaMuhurtham,
                showTamilDate = showTamilDate,
                showTamilYear = showTamilYear,
                showPirai = showPirai,
                showSunrise = showSunrise,
                showSunset = showSunset,
                showBrahmaMuhurtham = showBrahmaMuhurtham,
                showAbhijitMuhurtham = showAbhijitMuhurtham,
                isFallback = dayData.isFallback, 
                isLandscape = false, 
                is24Hour = is24Hour,
                onDetailClick = onDetailClick
            )
        }
        TimelineHeader(viewMode = viewMode, columnVisibility = columnVisibility, columnOrder = columnOrder)
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { containerHeight = it.size.height }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(hourHeight * (END_HOUR - START_HOUR))
                ) {
                    TimeGrid(hourHeight = hourHeight)
                    NightShade(sunrise = dayData.sunrise, sunset = dayData.sunset, hourHeight = hourHeight)

                    Row(modifier = Modifier.fillMaxSize()) {
                        TimeMarkersColumn(sunrise = dayData.sunrise, sunset = dayData.sunset, hourHeight = hourHeight, is24Hour = is24Hour)
                        Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(SeparatorGrey))
                        
                        if (viewMode == ViewMode.COMPOSITE) {
                            columnOrder.forEachIndexed { index, colId ->
                                if (columnVisibility.contains(colId)) {
                                    val timings = when (colId) {
                                        "NERAM" -> dayData.nallaNeram + dayData.specialPeriods
                                        "GOWRI" -> dayData.gowriNeram
                                        "HORA" -> dayData.hora
                                        else -> emptyList()
                                    }
                                    TimelineColumn(
                                        timings = timings, 
                                        onTimingClick = onTimingClick,
                                        hourHeight = hourHeight,
                                        is24Hour = is24Hour,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (index < columnOrder.size - 1 && columnVisibility.count { it in columnOrder.drop(index + 1) } > 0) {
                                        VerticalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        } else {
                            when (viewMode) {
                                ViewMode.NERAM -> TimelineColumn(dayData.nallaNeram + dayData.specialPeriods, onTimingClick, hourHeight, is24Hour, Modifier.weight(1f))
                                ViewMode.GOWRI -> TimelineColumn(dayData.gowriNeram, onTimingClick, hourHeight, is24Hour, Modifier.weight(1f))
                                ViewMode.HORA -> TimelineColumn(dayData.hora, onTimingClick, hourHeight, is24Hour, Modifier.weight(1f))
                                else -> {}
                            }
                        }
                    }

                    if (showSunrise) {
                        SunGridMarker(
                            time = dayData.sunrise, 
                            label = "Sunrise", 
                            icon = Icons.Default.WbSunny, 
                            iconTint = Color(0xFFFF9800),
                            hourHeight = hourHeight,
                            is24Hour = is24Hour
                        )
                    }
                    if (showSunset) {
                        SunGridMarker(
                            time = dayData.sunset, 
                            label = "Sunset", 
                            icon = Icons.Default.WbTwilight, 
                            iconTint = Color(0xFFFF5722),
                            hourHeight = hourHeight,
                            is24Hour = is24Hour
                        )
                    }

                    if (isToday && showNowLine) {
                        NowIndicator(
                            currentTime, 
                            hourHeight = hourHeight, 
                            color = Color(nowLineColor), 
                            thickness = nowLineThickness.dp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .width(TIME_COLUMN_WIDTH)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    onClick = onZoomIn,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp).align(Alignment.CenterHorizontally),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.label_zoom_in), modifier = Modifier.size(20.dp))
                    }
                }
                
                Surface(
                    onClick = onZoomOut,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp).align(Alignment.CenterHorizontally),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Remove, contentDescription = stringResource(R.string.label_zoom_out), modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineHeader(viewMode: ViewMode, columnVisibility: Set<String> = emptySet(), columnOrder: List<String> = emptyList()) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp)
            .height(IntrinsicSize.Min)
    ) {
        Spacer(modifier = Modifier.width(TIME_COLUMN_WIDTH))
        Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(SeparatorGrey))
        
        if (viewMode == ViewMode.COMPOSITE) {
            columnOrder.forEach { colId ->
                if (columnVisibility.contains(colId)) {
                    val labelRes = when (colId) {
                        "NERAM" -> R.string.nav_nalla_neram
                        "GOWRI" -> R.string.nav_gowri_neram
                        "HORA" -> R.string.nav_hora
                        else -> R.string.app_name
                    }
                    Text(
                        text = stringResource(labelRes),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            val labelRes = when (viewMode) {
                ViewMode.NERAM -> R.string.nav_nalla_neram
                ViewMode.GOWRI -> R.string.nav_gowri_neram
                ViewMode.HORA -> R.string.nav_hora
                else -> R.string.app_name
            }
            Text(stringResource(labelRes), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TimelineColumn(
    timings: List<Timing>,
    onTimingClick: (Timing) -> Unit,
    hourHeight: Dp,
    is24Hour: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxHeight()) {
        timings.forEach { timing ->
            TimingCard(timing = timing, hourHeight = hourHeight, is24Hour = is24Hour, onClick = { onTimingClick(timing) })
        }
    }
}
