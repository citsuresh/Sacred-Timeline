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
    isHeaderExpanded: Boolean = false,
    onToggleHeaderExpanded: (Boolean) -> Unit = {},
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
                            showAbhijitMuhurtham = showAbhijitMuhurtham,
                            isHeaderExpanded = isHeaderExpanded,
                            onToggleHeaderExpanded = onToggleHeaderExpanded
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
    isHeaderExpanded: Boolean = false,
    onToggleHeaderExpanded: (Boolean) -> Unit = {}
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
            val hasGoldEvent = dayData.isSubhaMuhurtham || dayData.specialEvents.any { 
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
                        
                        if (viewMode == ViewMode.COMPOSITE) {
                            columnOrder.forEachIndexed { index, colId ->
                                if (columnVisibility.contains(colId)) {
                                    val timings = when (colId) {
                                        "UNIVERSAL" -> {
                                            (dayData.nallaNeram + dayData.specialPeriods + 
                                             dayData.gowriNeram + dayData.hora +
                                             listOfNotNull(dayData.brahmaMuhurtham, dayData.abhijitMuhurtham))
                                                .sortedBy { it.startTime }
                                        }
                                        "NERAM_MUHURTHAM" -> {
                                            (dayData.nallaNeram + dayData.specialPeriods +
                                             listOfNotNull(dayData.brahmaMuhurtham, dayData.abhijitMuhurtham))
                                                .sortedBy { it.startTime }
                                        }
                                        "NERAM" -> dayData.nallaNeram + dayData.specialPeriods
                                        "BRAHMA" -> listOfNotNull(dayData.brahmaMuhurtham)
                                        "ABHIJIT" -> listOfNotNull(dayData.abhijitMuhurtham)
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
                            val timings = when (viewMode) {
                                ViewMode.UNIVERSAL -> {
                                    (dayData.nallaNeram + dayData.specialPeriods + 
                                     dayData.gowriNeram + dayData.hora +
                                     listOfNotNull(dayData.brahmaMuhurtham, dayData.abhijitMuhurtham))
                                        .sortedBy { it.startTime }
                                }
                                ViewMode.NERAM_MUHURTHAM -> {
                                    (dayData.nallaNeram + dayData.specialPeriods +
                                     listOfNotNull(dayData.brahmaMuhurtham, dayData.abhijitMuhurtham))
                                        .sortedBy { it.startTime }
                                }
                                ViewMode.NERAM -> dayData.nallaNeram + dayData.specialPeriods
                                ViewMode.BRAHMA -> listOfNotNull(dayData.brahmaMuhurtham)
                                ViewMode.ABHIJIT -> listOfNotNull(dayData.abhijitMuhurtham)
                                ViewMode.GOWRI -> dayData.gowriNeram
                                ViewMode.HORA -> dayData.hora
                                ViewMode.COMPOSITE -> emptyList()
                            }
                            if (timings.isNotEmpty()) {
                                TimelineColumn(
                                    timings = timings, 
                                    onTimingClick = onTimingClick, 
                                    hourHeight = hourHeight, 
                                    is24Hour = is24Hour, 
                                    modifier = Modifier.weight(1f)
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
            
            if (viewMode == ViewMode.COMPOSITE) {
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
) {
    Box(modifier = modifier.fillMaxHeight()) {
        val lanes = remember(timings) { calculateLanes(timings) }
        
        lanes.forEach { (timing, laneInfo) ->
            TimingCard(
                timing = timing, 
                hourHeight = hourHeight, 
                is24Hour = is24Hour,
                widthFactor = laneInfo.widthFactor,
                horizontalOffsetFactor = laneInfo.offsetFactor,
                onClick = { onTimingClick(timing) }
            )
        }
    }
}

private data class LaneInfo(
    val widthFactor: Float,
    val offsetFactor: Float
)

private fun calculateLanes(timings: List<Timing>): Map<Timing, LaneInfo> {
    if (timings.isEmpty()) return emptyMap()

    val result = mutableMapOf<Timing, LaneInfo>()
    
    // 1. Group timings that belong to the same 'track'
    val gowri = timings.filterIsInstance<GowriNeram>()
    val horai = timings.filterIsInstance<Hora>()
    val middle = timings.filter { it !is GowriNeram && it !is Hora }

    // 2. For each track, resolve internal overlaps by assigning sub-indices
    val gowriSubLanes = resolveInternalLanes(gowri)
    val horaiSubLanes = resolveInternalLanes(horai)
    val middleSubLanes = resolveInternalLanes(middle)

    // 3. For each item, calculate how many tracks are active during its span
    timings.forEach { timing ->
        val overlapping = timings.filter { overlaps(it, timing) }
        
        // Find which tracks are represented in this span
        val hasGowri = overlapping.any { it is GowriNeram }
        val hasMiddle = overlapping.any { it !is GowriNeram && it !is Hora }
        val hasHorai = overlapping.any { it is Hora }
        
        val activeTracks = mutableListOf<Int>()
        if (hasGowri) activeTracks.add(0)
        if (hasMiddle) activeTracks.add(1)
        if (hasHorai) activeTracks.add(2)
        
        val totalTracks = activeTracks.size
        val trackWidth = 1.0f / totalTracks
        
        val myTrack = when (timing) {
            is GowriNeram -> 0
            is Hora -> 2
            else -> 1
        }
        val myTrackIndex = activeTracks.indexOf(myTrack)
        
        // Now factor in internal track sub-division
        val subLaneInfo = when (timing) {
            is GowriNeram -> gowriSubLanes[timing]!!
            is Hora -> horaiSubLanes[timing]!!
            else -> middleSubLanes[timing]!!
        }
        
        // To maintain equal distribution, we find the max sub-lanes needed for this track 
        // during this specific item's span.
        val itemsInMyTrackInSpan = overlapping.filter { 
            when (it) {
                is GowriNeram -> myTrack == 0
                is Hora -> myTrack == 2
                else -> myTrack == 1
            }
        }
        val maxSubLanesInTrack = itemsInMyTrackInSpan.map { 
            when (it) {
                is GowriNeram -> gowriSubLanes[it]!!.total
                is Hora -> horaiSubLanes[it]!!.total
                else -> middleSubLanes[it]!!.total
            }
        }.maxOrNull() ?: 1

        val finalWidth = trackWidth / maxSubLanesInTrack
        val trackOffset = myTrackIndex * trackWidth
        val subOffset = subLaneInfo.index * finalWidth
        
        result[timing] = LaneInfo(
            widthFactor = finalWidth,
            offsetFactor = trackOffset + subOffset
        )
    }

    return result
}

private data class SubLaneInfo(val index: Int, val total: Int)

private fun resolveInternalLanes(items: List<Timing>): Map<Timing, SubLaneInfo> {
    if (items.isEmpty()) return emptyMap()
    val result = mutableMapOf<Timing, Int>()
    val sorted = items.sortedBy { it.startTime }
    
    sorted.forEach { t ->
        val overlapping = sorted.filter { result.containsKey(it) && overlaps(it, t) }
        val used = overlapping.map { result[it]!! }
        var idx = 0
        while (used.contains(idx)) idx++
        result[t] = idx
    }
    
    // Calculate total concurrent for each item's specific span
    return result.mapValues { (t, idx) ->
        val overlapping = items.filter { overlaps(it, t) }
        // The number of sub-lanes needed is the max concurrent at any point in t's range
        val timePoints = (overlapping.map { it.startTime } + overlapping.map { it.endTime })
            .filter { !it.isBefore(t.startTime) && !it.isAfter(t.endTime) }
            .distinct().sorted()
        
        var maxC = 1
        for (i in 0 until timePoints.size - 1) {
            val count = overlapping.count { it.startTime.isBefore(timePoints[i+1]) && it.endTime.isAfter(timePoints[i]) }
            maxC = maxOf(maxC, count)
        }
        SubLaneInfo(idx, maxC)
    }
}

private fun overlaps(t1: Timing, t2: Timing): Boolean {
    return t1.startTime.isBefore(t2.endTime) && t2.startTime.isBefore(t1.endTime)
}
