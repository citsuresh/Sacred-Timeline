package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.abs
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.*
import com.suresh.sacredtimeline.ui.dashboard.TimelineUiState
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import com.suresh.sacredtimeline.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

enum class TimelineViewStyle {
    FIXED_3_TRACK,
    EQUAL_DISTRIBUTION,
    ORTHOGONAL_STEPPED
}

private val BASE_HOUR_HEIGHT = 160.dp
private const val START_HOUR = 0
private const val END_HOUR = 24
private val TIME_COLUMN_WIDTH = 65.dp

data class FullDayEvent(
    val label: String,
    val color: Color,
    val backgroundColor: Color
)

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
    showMaitraMuhurtham: Boolean = true,
    isHeaderExpanded: Boolean = false,
    onToggleHeaderExpanded: (Boolean) -> Unit = {},
    onScaleChange: (Float) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onDetailClick: (com.suresh.sacredtimeline.model.DashboardDetail) -> Unit = {},
    isLandscape: Boolean,
    viewStyle: TimelineViewStyle = TimelineViewStyle.EQUAL_DISTRIBUTION
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
                            showAbhijitMuhurtham = showAbhijitMuhurtham,
                            isHeaderExpanded = isHeaderExpanded,
                            onToggleHeaderExpanded = onToggleHeaderExpanded,
                            viewStyle = viewStyle
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
    showAbhijitMuhurtham: Boolean = false,
    showMaitraMuhurtham: Boolean = true,
    isHeaderExpanded: Boolean = false,
    onToggleHeaderExpanded: (Boolean) -> Unit = {},
    viewStyle: TimelineViewStyle = TimelineViewStyle.EQUAL_DISTRIBUTION
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
        Column(
            modifier = Modifier.pointerInput(isHeaderExpanded) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var totalX = 0f
                    var totalY = 0f
                    var decided = false
                    var isVertical = false
                    
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.first()
                        if (change.changedToUp()) break
                        
                        val delta = change.positionChange()
                        totalX += abs(delta.x)
                        totalY += abs(delta.y)
                        
                        if (!decided) {
                            val slop = viewConfiguration.touchSlop
                            if (totalY > slop || totalX > slop) {
                                decided = true
                                // Prioritize vertical if it's the dominant direction
                                isVertical = totalY > totalX
                            }
                        }
                        
                        if (decided && isVertical) {
                            change.consume()
                            // Accumulate Y for the threshold
                            if (totalY > 25) {
                                if (delta.y > 0 && !isHeaderExpanded) {
                                    onToggleHeaderExpanded(true)
                                } else if (delta.y < 0 && isHeaderExpanded) {
                                    onToggleHeaderExpanded(false)
                                }
                            }
                        }
                        
                        if (change.isConsumed && !isVertical) break
                    }
                }
            }
        ) {
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
                    isExpanded = isHeaderExpanded,
                    onToggleExpanded = onToggleHeaderExpanded,
                    isFallback = dayData.isFallback, 
                    isLandscape = false, 
                    is24Hour = is24Hour,
                    onDetailClick = onDetailClick
                )
            }
            TimelineHeader(
                viewMode = viewMode, 
                columnVisibility = columnVisibility, 
                columnOrder = columnOrder,
                fullDayEvents = buildList {
                    if (dayData.isSubhaMuhurtham) {
                        add(FullDayEvent(stringResource(R.string.muhurtham_subha), SubhaMuhurthamGold, SubhaMuhurthamPale))
                    }
                    dayData.specialEvents.forEach { resId ->
                        if (resId != R.string.event_pradosham && resId != R.string.event_sivaratri) {
                            val (c, bg) = SacredTimelineColors.getEventColors(resId)
                            add(FullDayEvent(stringResource(resId), c, bg))
                        }
                    }
                }
            )
        }
        
        val currentBackgroundTint = remember(dayData) {
            val hasGoldEvent = dayData.isSubhaMuhurtham || dayData.maitraMuhurtham.isNotEmpty() || dayData.specialEvents.any { 
                SacredTimelineColors.getEventColors(it).first == SubhaMuhurthamGold 
            }
            val hasPurpleEvent = dayData.specialEvents.any { 
                SacredTimelineColors.getEventColors(it).first == HolidayPurple 
            }

            when {
                hasGoldEvent -> SubhaMuhurthamPale
                hasPurpleEvent -> HolidayPale
                else -> null
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { containerHeight = it.size.height }
                .then(if (currentBackgroundTint != null) Modifier.background(currentBackgroundTint) else Modifier)
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
                        
                        val visibleCols = columnOrder.filter { columnVisibility.contains(it) }
                        val isCompositeMode = viewMode == ViewMode.COMPOSITE || viewMode == ViewMode.CUSTOM
                        val isMergedLayout = viewStyle == TimelineViewStyle.ORTHOGONAL_STEPPED || viewStyle == TimelineViewStyle.FIXED_3_TRACK

                        if (isCompositeMode && !isMergedLayout) {
                            // Independent Lanes for Equal Distribution
                            visibleCols.forEachIndexed { index, colId ->
                                val timings = when (colId) {
                                    "UNIVERSAL" -> (dayData.nallaNeram + dayData.specialPeriods + dayData.gowriNeram + dayData.hora + (if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList()) + (if (showBrahmaMuhurtham) listOfNotNull(dayData.brahmaMuhurtham) else emptyList()) + (if (showAbhijitMuhurtham) listOfNotNull(dayData.abhijitMuhurtham) else emptyList())).sortedBy { it.startTime }
                                    "NERAM_MUHURTHAM" -> (dayData.nallaNeram + dayData.specialPeriods + (if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList()) + (if (showBrahmaMuhurtham) listOfNotNull(dayData.brahmaMuhurtham) else emptyList()) + (if (showAbhijitMuhurtham) listOfNotNull(dayData.abhijitMuhurtham) else emptyList())).sortedBy { it.startTime }
                                    "MAITRA" -> if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList()
                                    "NERAM" -> (dayData.nallaNeram + dayData.specialPeriods + (if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList())).sortedBy { it.startTime }
                                    "BRAHMA" -> if (showBrahmaMuhurtham) listOfNotNull(dayData.brahmaMuhurtham) else emptyList()
                                    "ABHIJIT" -> if (showAbhijitMuhurtham) listOfNotNull(dayData.abhijitMuhurtham) else emptyList()
                                    "GOWRI" -> dayData.gowriNeram
                                    "HORA" -> dayData.hora
                                    else -> emptyList()
                                }
                                TimelineColumn(
                                    timings = timings,
                                    onTimingClick = onTimingClick,
                                    hourHeight = hourHeight,
                                    is24Hour = is24Hour,
                                    modifier = Modifier.weight(1f),
                                    viewStyle = viewStyle
                                )
                                if (index < visibleCols.size - 1) {
                                    VerticalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
                                }
                            }
                        } else {
                            // Merged Layout (Universal, Solo, or Composite-Merged)
                            val timings = if (viewMode == ViewMode.COMPOSITE || viewMode == ViewMode.CUSTOM) {
                                visibleCols.flatMap { colId ->
                                    when (colId) {
                                    "UNIVERSAL" -> (dayData.nallaNeram + dayData.specialPeriods + dayData.gowriNeram + dayData.hora + (if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList()) + (if (showBrahmaMuhurtham) listOfNotNull(dayData.brahmaMuhurtham) else emptyList()) + (if (showAbhijitMuhurtham) listOfNotNull(dayData.abhijitMuhurtham) else emptyList()))
                                    "NERAM_MUHURTHAM" -> (dayData.nallaNeram + dayData.specialPeriods + (if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList()) + (if (showBrahmaMuhurtham) listOfNotNull(dayData.brahmaMuhurtham) else emptyList()) + (if (showAbhijitMuhurtham) listOfNotNull(dayData.abhijitMuhurtham) else emptyList()))
                                    "MAITRA" -> if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList()
                                    "NERAM" -> (dayData.nallaNeram + dayData.specialPeriods + (if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList()))
                                    "BRAHMA" -> if (showBrahmaMuhurtham) listOfNotNull(dayData.brahmaMuhurtham) else emptyList()
                                    "ABHIJIT" -> if (showAbhijitMuhurtham) listOfNotNull(dayData.abhijitMuhurtham) else emptyList()
                                    "GOWRI" -> dayData.gowriNeram
                                    "HORA" -> dayData.hora
                                    else -> emptyList()
                                }
                            }.distinct().sortedBy { it.startTime }
                        } else {
                                when (viewMode) {
                                    ViewMode.UNIVERSAL -> (dayData.nallaNeram + dayData.specialPeriods + dayData.gowriNeram + dayData.hora + (if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList()) + (if (showBrahmaMuhurtham) listOfNotNull(dayData.brahmaMuhurtham) else emptyList()) + (if (showAbhijitMuhurtham) listOfNotNull(dayData.abhijitMuhurtham) else emptyList())).sortedBy { it.startTime }
                                    ViewMode.NERAM_MUHURTHAM -> (dayData.nallaNeram + dayData.specialPeriods + (if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList()) + (if (showBrahmaMuhurtham) listOfNotNull(dayData.brahmaMuhurtham) else emptyList()) + (if (showAbhijitMuhurtham) listOfNotNull(dayData.abhijitMuhurtham) else emptyList())).sortedBy { it.startTime }
                                    ViewMode.NERAM -> (dayData.nallaNeram + dayData.specialPeriods + (if (showMaitraMuhurtham) dayData.maitraMuhurtham else emptyList())).sortedBy { it.startTime }
                                    ViewMode.BRAHMA -> listOfNotNull(dayData.brahmaMuhurtham)
                                    ViewMode.ABHIJIT -> listOfNotNull(dayData.abhijitMuhurtham)
                                    ViewMode.GOWRI -> dayData.gowriNeram
                                    ViewMode.HORA -> dayData.hora
                                    ViewMode.MAITRA -> dayData.maitraMuhurtham
                                    else -> emptyList()
                                }
                            }
                            if (timings.isNotEmpty()) {
                                TimelineColumn(
                                    timings = timings,
                                    onTimingClick = onTimingClick,
                                    hourHeight = hourHeight,
                                    is24Hour = is24Hour,
                                    modifier = Modifier.weight(1f),
                                    viewStyle = viewStyle,
                                    pillarConfig = if (viewStyle == TimelineViewStyle.FIXED_3_TRACK) {
                                        val leftCat = visibleCols.firstOrNull() ?: ""
                                        val rightCat = visibleCols.lastOrNull().takeIf { it != leftCat } ?: ""
                                        PillarConfig(leftCat, rightCat)
                                    } else null
                                )
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
fun TimelineHeader(
    viewMode: ViewMode, 
    columnVisibility: Set<String> = emptySet(), 
    columnOrder: List<String> = emptyList(),
    fullDayEvents: List<FullDayEvent> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .height(IntrinsicSize.Min)
        ) {
            Spacer(modifier = Modifier.width(TIME_COLUMN_WIDTH))
            Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(SeparatorGrey))
            
            val isCompositeMode = viewMode == ViewMode.COMPOSITE || viewMode == ViewMode.CUSTOM
            if (isCompositeMode) {
                columnOrder.forEach { colId ->
                    if (columnVisibility.contains(colId)) {
                        val text = when (colId) {
                            "UNIVERSAL" -> stringResource(R.string.nav_universal)
                            "NERAM_MUHURTHAM" -> stringResource(R.string.nav_neram_muhurtham)
                            "NERAM" -> stringResource(R.string.nav_nalla_neram)
                            "BRAHMA" -> stringResource(R.string.muhurtham_brahma)
                            "ABHIJIT" -> stringResource(R.string.muhurtham_abhijit)
                            "GOWRI" -> stringResource(R.string.nav_gowri_neram)
                            "HORA" -> stringResource(R.string.nav_hora)
                            "MAITRA" -> stringResource(R.string.timing_maitra)
                            else -> stringResource(R.string.app_name)
                        }
                        Text(
                            text = text,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                val labelRes = when (viewMode) {
                    ViewMode.UNIVERSAL -> R.string.nav_universal
                    ViewMode.NERAM_MUHURTHAM -> R.string.nav_neram_muhurtham
                    ViewMode.NERAM -> R.string.nav_nalla_neram
                    ViewMode.BRAHMA -> R.string.muhurtham_brahma
                    ViewMode.ABHIJIT -> R.string.muhurtham_abhijit
                    ViewMode.GOWRI -> R.string.nav_gowri_neram
                    ViewMode.HORA -> R.string.nav_hora
                    ViewMode.MAITRA -> R.string.nav_maitra
                    ViewMode.CUSTOM -> R.string.nav_custom
                    ViewMode.COMPOSITE -> R.string.app_name
                }
                Text(stringResource(labelRes), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        if (fullDayEvents.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(TIME_COLUMN_WIDTH))
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    fullDayEvents.forEach { event ->
                        Surface(
                            color = event.color,
                            shape = RoundedCornerShape(4.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = event.label.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineColumn(
    timings: List<Timing>,
    onTimingClick: (Timing) -> Unit,
    hourHeight: Dp,
    is24Hour: Boolean,
    modifier: Modifier = Modifier,
    viewStyle: TimelineViewStyle = TimelineViewStyle.EQUAL_DISTRIBUTION,
    pillarConfig: PillarConfig? = null
) {
    Box(modifier = modifier.fillMaxHeight()) {
        val segmentsMap = remember(timings, viewStyle, pillarConfig) {
            calculateLanes(timings, viewStyle, pillarConfig)
        }
        
        segmentsMap.forEach { (timing, segments) ->
            TimingCard(
                timing = timing, 
                hourHeight = hourHeight, 
                is24Hour = is24Hour,
                segments = segments,
                onClick = { onTimingClick(timing) }
            )
        }
    }
}

data class LaneSegment(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val widthFactor: Float,
    val offsetFactor: Float
)

data class PillarConfig(val leftCategory: String, val rightCategory: String)

private fun calculateLanes(
    timings: List<Timing>,
    style: TimelineViewStyle,
    pillarConfig: PillarConfig? = null
): Map<Timing, List<LaneSegment>> {
    if (timings.isEmpty()) return emptyMap()

    // 1. Group items into transitive clusters
    val clusters = mutableListOf<MutableSet<Timing>>()
    timings.forEach { timing ->
        val overlappingClusters = clusters.filter { cluster ->
            cluster.any { overlaps(it, timing) }
        }
        
        if (overlappingClusters.isEmpty()) {
            clusters.add(mutableSetOf(timing))
        } else {
            val combined = overlappingClusters.reduce { acc, set -> acc.apply { addAll(set) } }
            combined.add(timing)
            clusters.removeAll(overlappingClusters)
            clusters.add(combined)
        }
    }

    val result = mutableMapOf<Timing, MutableList<LaneSegment>>()
    timings.forEach { result[it] = mutableListOf() }

    // 2. Process each cluster independently
    clusters.forEach { cluster ->
        val items = cluster.toList()
        
        when (style) {
            TimelineViewStyle.FIXED_3_TRACK -> {
                // If the user selects the "UNIVERSAL" bundle as a pillar, 
                // we interpret it as a request for the standard Traditional pillars.
                val leftCat = if (pillarConfig?.leftCategory == "UNIVERSAL") "GOWRI" else (pillarConfig?.leftCategory ?: "GOWRI")
                val rightCat = if (pillarConfig?.leftCategory == "UNIVERSAL") "HORA" else (pillarConfig?.rightCategory ?: "HORA")

                val leftItems = items.filter { it.getCategory() == leftCat }.sortedBy { it.startTime }
                val rightItems = items.filter { it.getCategory() == rightCat }.sortedBy { it.startTime }
                val centerItems = items.filter { it.getCategory() != leftCat && it.getCategory() != rightCat }
                    .sortedWith(compareBy<Timing> { it.startTime }.thenByDescending { it is MaitraMuhurtham })

                fun assignFixed(list: List<Timing>, trackIndex: Int) {
                    val trackWidth = 1.0f / 3.0f
                    val lanes = mutableListOf<MutableList<Timing>>()
                    list.forEach { t ->
                        var placed = false
                        for (l in lanes) if (l.none { overlaps(it, t) }) { l.add(t); placed = true; break }
                        if (!placed) lanes.add(mutableListOf(t))
                    }
                    val subWidth = trackWidth / (lanes.size.coerceAtLeast(1))
                    lanes.forEachIndexed { lIdx, lItems ->
                        lItems.forEach { t ->
                            result[t]?.add(LaneSegment(t.startTime, t.endTime, subWidth, (trackIndex * trackWidth) + (lIdx * subWidth)))
                        }
                    }
                }
                assignFixed(leftItems, 0)
                assignFixed(centerItems, 1)
                assignFixed(rightItems, 2)
            }

            TimelineViewStyle.EQUAL_DISTRIBUTION -> {
                val itemsGowri = items.filter { it is GowriNeram }.sortedBy { it.startTime }
                val itemsHorai = items.filter { it is Hora }.sortedBy { it.startTime }
                val itemsCenter = items.filter { it !is GowriNeram && it !is Hora }
                    .sortedWith(compareBy<Timing> { it.startTime }.thenByDescending { it is MaitraMuhurtham })

                fun assign(categoryItems: List<Timing>): List<List<Timing>> {
                    val lanes = mutableListOf<MutableList<Timing>>()
                    categoryItems.forEach { timing ->
                        var placed = false
                        for (lane in lanes) if (lane.none { overlaps(it, timing) }) { lane.add(timing); placed = true; break }
                        if (!placed) lanes.add(mutableListOf(timing))
                    }
                    return lanes
                }

                val gLanes = assign(itemsGowri)
                val cLanes = assign(itemsCenter)
                val hLanes = assign(itemsHorai)

                // 1. Initial Lane Anchors (Fractions)
                val totalLanesCount = (gLanes.size + cLanes.size + hLanes.size).coerceAtLeast(1)
                val laneWidth = 1.0f / totalLanesCount
                val itemBounds = mutableMapOf<Timing, Pair<Float, Float>>()

                gLanes.forEachIndexed { i, l -> l.forEach { itemBounds[it] = (i * laneWidth) to ((i + 1) * laneWidth) } }
                cLanes.forEachIndexed { i, l -> l.forEach { itemBounds[it] = ((gLanes.size + i) * laneWidth) to ((gLanes.size + i + 1) * laneWidth) } }
                hLanes.forEachIndexed { i, l -> l.forEach { itemBounds[it] = ((gLanes.size + cLanes.size + i) * laneWidth) to ((gLanes.size + cLanes.size + i + 1) * laneWidth) } }

                // 2. Iterative Co-operative Refinement (8 passes for near-perfect gap filling)
                repeat(8) {
                    items.forEach { t ->
                        val bounds = itemBounds[t] ?: return@forEach
                        val currentStart = bounds.first
                        val currentEnd = bounds.second
                        
                        // Find constraint boundaries (closest overlapping neighbors)
                        val leftNeighbors = items.filter { it != t && overlaps(it, t) && (itemBounds[it]?.second ?: 0f) <= (currentStart + 0.001f) }
                        val leftBoundary = leftNeighbors.maxOfOrNull { itemBounds[it]?.second ?: 0f } ?: 0f
                        
                        val rightNeighbors = items.filter { it != t && overlaps(it, t) && (itemBounds[it]?.first ?: 1f) >= (currentEnd - 0.001f) }
                        val rightBoundary = rightNeighbors.minOfOrNull { itemBounds[it]?.first ?: 1f } ?: 1f
                        
                        // Co-operative Expand: Move halfway toward the empty space
                        val refinedStart = currentStart - (currentStart - leftBoundary) / 2f
                        val refinedEnd = currentEnd + (rightBoundary - currentEnd) / 2f
                        
                        itemBounds[t] = refinedStart to refinedEnd
                    }
                }

                itemBounds.forEach { (t, bounds) ->
                    result[t]?.add(LaneSegment(t.startTime, t.endTime, bounds.second - bounds.first, bounds.first))
                }
            }

            TimelineViewStyle.ORTHOGONAL_STEPPED -> {
                val timePoints = (items.map { it.startTime } + items.map { it.endTime }).distinct().sorted()
                for (i in 0 until timePoints.size - 1) {
                    val sliceStart = timePoints[i]
                    val sliceEnd = timePoints[i+1]
                    val activeItems = items.filter { !it.startTime.isAfter(sliceStart) && it.endTime.isAfter(sliceStart) }
                        .sortedWith(compareBy({ when(it) { is GowriNeram -> 0; is Hora -> 2; else -> 1 } }, { it.startTime }))
                    
                    val width = 1.0f / activeItems.size.coerceAtLeast(1)
                    activeItems.forEachIndexed { idx, t ->
                        result[t]?.add(LaneSegment(sliceStart, sliceEnd, width, idx * width))
                    }
                }
            }
        }
    }

    return result.mapValues { it.value.toList() }
}

// Extension function to map timings to our column management IDs
private fun Timing.getCategory(): String = when (this) {
    is GowriNeram -> "GOWRI"
    is Hora -> "HORA"
    is NallaNeram -> "NERAM"
    is SpecialPeriod -> "NERAM"
    is MaitraMuhurtham -> "MAITRA"
    is Muhurtham -> {
        if (this.name.contains("Brahma")) "BRAHMA"
        else if (this.name.contains("Abhijit")) "ABHIJIT"
        else "NERAM_MUHURTHAM"
    }
    else -> "UNIVERSAL"
}

private fun overlaps(t1: Timing, t2: Timing): Boolean {
    return t1.startTime.isBefore(t2.endTime) && t2.startTime.isBefore(t1.endTime)
}
