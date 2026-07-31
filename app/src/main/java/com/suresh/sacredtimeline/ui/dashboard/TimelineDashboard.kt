package com.suresh.sacredtimeline.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Block
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.res.painterResource
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.*
import com.suresh.sacredtimeline.ui.theme.*
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val HOUR_HEIGHT = 160.dp
private val START_HOUR = 0
private val END_HOUR = 24
private val TIME_COLUMN_WIDTH = 60.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@Composable
fun TimelineDashboard(
    modifier: Modifier = Modifier,
    viewModel: TimelineViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    
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
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Change Location") },
            text = {
                Column {
                    Text("Enter city or area name manually for display.", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = manualLocationName,
                        onValueChange = { manualLocationName = it },
                        label = { Text("Location Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Note: This only changes the display name. Timings remain calculated based on your current GPS location.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (manualLocationName.isNotBlank()) {
                        viewModel.updateManualLocation(manualLocationName)
                    }
                    showLocationDialog = false
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val dates = remember {
        (-30..30).map { LocalDate.now().plusDays(it.toLong()) }
    }
    val initialPage = dates.indexOf(selectedDate).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { dates.size })

    // Sync Pager with selectedDate from external sources (DatePicker, Dial)
    LaunchedEffect(selectedDate) {
        val page = dates.indexOf(selectedDate)
        if (page != -1 && page != pagerState.currentPage) {
            pagerState.animateScrollToPage(page)
        }
    }

    // Sync selectedDate with Pager swipe
    LaunchedEffect(pagerState.currentPage) {
        val date = dates[pagerState.currentPage]
        if (date != selectedDate) {
            viewModel.updateDate(date)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Sacred Timeline",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.updateDate(LocalDate.now()) }) {
                        Icon(Icons.Filled.Today, contentDescription = "Today")
                    }
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date")
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
        Column(modifier = modifier.padding(padding)) {
            // Dedicated Date & Location Info Row
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                    if (uiState is TimelineUiState.Success) {
                        val successState = uiState as TimelineUiState.Success
                        val isUnknown = successState.locationName == "Unknown Location"
                        
                        Text(
                            text = " • ${if (successState.isLocationAuto) "📍" else "✎"} ${successState.locationName}",
                            style = if (successState.isLocationAuto) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelLarge.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = if (isUnknown) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .clickable { 
                                    manualLocationName = successState.locationName
                                    showLocationDialog = true 
                                }
                        )
                    }
                }
            }

            HorizontalDateDial(
                selectedDate = selectedDate,
                dates = dates,
                onDateSelected = { viewModel.updateDate(it) }
            )
            
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is TimelineUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is TimelineUiState.Success -> {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val date = dates[page]
                            val dayData = state.days[date]
                            if (dayData != null) {
                                TimelineContent(date, dayData)
                            } else {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalDateDial(
    selectedDate: LocalDate,
    dates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(selectedDate) {
        val index = dates.indexOf(selectedDate)
        if (index != -1) {
            scrollState.animateScrollToItem((index - 2).coerceAtLeast(0))
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDateSelected(selectedDate.minusDays(1)) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous Day",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        LazyRow(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(dates) { date ->
                DateItem(
                    date = date,
                    isSelected = date == selectedDate,
                    onClick = { onDateSelected(date) }
                )
            }
        }

        IconButton(onClick = { onDateSelected(selectedDate.plusDays(1)) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next Day",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayFormatter = DateTimeFormatter.ofPattern("EEE")
    val dateFormatter = DateTimeFormatter.ofPattern("dd")
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.format(dayFormatter),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = date.format(dateFormatter),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TimelineContent(date: LocalDate, dayData: DayData) {
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
    
    // Auto-scroll to current time on first launch for today
    LaunchedEffect(containerHeight) {
        if (containerHeight > 0 && isToday) {
            val nowOffset = with(density) { calculateOffset(currentTime).toPx() }
            val centerOffset = containerHeight / 2
            scrollState.scrollTo((nowOffset - centerOffset).toInt().coerceAtLeast(0))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SunTimesHeader(dayData.sunrise, dayData.sunset, dayData.isFallback)
        TimelineHeader()
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { containerHeight = it.size.height }
        ) {
            // Timeline scrollable area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HOUR_HEIGHT * (END_HOUR - START_HOUR))
                ) {
                    TimeGrid()
                    
                    // Night Shade (Sunset to Sunrise/End of day)
                    NightShade(sunrise = dayData.sunrise, sunset = dayData.sunset)

                    Row(modifier = Modifier.fillMaxSize()) {
                        TimeMarkersColumn()
                        Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(SeparatorGrey))
                        TimelineColumn(timings = dayData.nallaNeram + dayData.specialPeriods, modifier = Modifier.weight(1f))
                        VerticalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        TimelineColumn(timings = dayData.gowriNeram, modifier = Modifier.weight(1f))
                        VerticalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        TimelineColumn(timings = dayData.hora, modifier = Modifier.weight(1f))
                    }

                    // Sun Markers
                    SunGridMarker(
                        time = dayData.sunrise, 
                        label = "Sunrise", 
                        icon = Icons.Default.WbSunny, 
                        iconTint = Color(0xFFFF9800),
                        isFallback = dayData.isFallback
                    )
                    SunGridMarker(
                        time = dayData.sunset, 
                        label = "Sunset", 
                        icon = Icons.Default.WbTwilight, 
                        iconTint = Color(0xFFFF5722),
                        isFallback = dayData.isFallback
                    )

                    // Dynamic Current Time Indicator (inside scrollable area)
                    if (isToday) {
                        NowIndicator(currentTime)
                    }
                }
            }
        }
    }
}

@Composable
fun NightShade(sunrise: LocalTime, sunset: LocalTime) {
    val sunsetOffset = calculateOffset(sunset)
    val sunriseOffset = calculateOffset(sunrise)
    
    // Part 1: Midnight to Sunrise
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(sunriseOffset)
            .background(Color.Black.copy(alpha = 0.15f))
    )
    
    // Part 2: Sunset to Midnight (End of Day)
    val dayEndOffset = calculateOffset(LocalTime.MAX)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = sunsetOffset)
            .height(dayEndOffset - sunsetOffset)
            .background(Color.Black.copy(alpha = 0.15f))
    )
}

@Composable
fun SunGridMarker(time: LocalTime, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, isFallback: Boolean) {
    val topOffset = calculateOffset(time)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    val sectionLabel = if (label == "Sunrise") "Day Muhurat" else "Night Muhurat"
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = topOffset - 14.dp)
            .padding(start = 4.dp, end = 4.dp)
    ) {
        // Full width separator line with glow
        Canvas(modifier = Modifier.fillMaxWidth().height(2.dp).offset(y = 14.dp)) {
            // Subtle glow
            drawRect(
                color = iconTint.copy(alpha = 0.3f),
                topLeft = Offset(0f, -4f),
                size = androidx.compose.ui.geometry.Size(size.width, 10f)
            )
            // Solid line
            drawLine(
                color = iconTint,
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = 4f
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = TIME_COLUMN_WIDTH, end = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Section Label
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.9f))
                    .border(2.dp, iconTint.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (label == "Sunrise") "🌅" else "🌇",
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = sectionLabel,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Right: Time Label
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.9f))
                    .border(2.dp, iconTint.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    modifier = Modifier.size(14.dp),
                    tint = iconTint
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${if (label == "Sunrise") "🌅" else "🌇"} $label: ${time.format(formatter)}${if (isFallback) " (approx)" else ""}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun BoxScope.NowIndicator(currentTime: LocalTime) {
    val topOffset = calculateOffset(currentTime)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = topOffset - 1.dp)
            .height(2.dp)
            .background(Color.Red)
    )
    
    Surface(
        color = Color.Red,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .offset(y = topOffset - 10.dp)
            .padding(start = 8.dp)
            .align(Alignment.TopStart)
    ) {
        Text(
            text = "NOW",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SunTimesHeader(sunrise: LocalTime, sunset: LocalTime, isFallback: Boolean) {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            .padding(vertical = 4.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.WbSunny, 
                contentDescription = null, 
                modifier = Modifier.size(16.dp),
                tint = Color(0xFFFF9800)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("🌅 Sunrise: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(sunrise.format(formatter), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        if (isFallback) {
            Text("(approximate)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), fontSize = 10.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.WbTwilight, 
                contentDescription = null, 
                modifier = Modifier.size(16.dp),
                tint = Color(0xFFFF5722) // Deep Orange/Sunset color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("🌇 Sunset: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(sunset.format(formatter), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
fun TimelineHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp)
            .height(IntrinsicSize.Min)
    ) {
        Spacer(modifier = Modifier.width(TIME_COLUMN_WIDTH))
        Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(SeparatorGrey))
        Text("Neram", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text("Gowri Neram", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text("Hora", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TimeMarkersColumn() {
    val context = LocalContext.current
    val is24Hour = android.text.format.DateFormat.is24HourFormat(context)
    val pattern = if (is24Hour) "HH:mm" else "h:mm a"
    val formatter = DateTimeFormatter.ofPattern(pattern)

    Box(
        modifier = Modifier
            .width(TIME_COLUMN_WIDTH)
            .fillMaxHeight()
    ) {
        for (hour in START_HOUR until END_HOUR) {
            for (minute in listOf(0, 30)) {
                val time = LocalTime.of(hour, minute)
                val topOffset = calculateOffset(time)
                
                Text(
                    text = time.format(formatter),
                    modifier = Modifier
                        .offset(y = topOffset - 8.dp)
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun TimeGrid() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 1.dp.toPx()
        for (hour in START_HOUR..END_HOUR) {
            // Hour line
            val yHour = (hour - START_HOUR) * HOUR_HEIGHT.toPx()
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(0f, yHour),
                end = Offset(size.width, yHour),
                strokeWidth = strokeWidth
            )
            
            // 30-min line
            if (hour < END_HOUR) {
                val yHalf = yHour + (HOUR_HEIGHT.toPx() / 2)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    start = Offset(0f, yHalf),
                    end = Offset(size.width, yHalf),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

@Composable
fun TimelineColumn(
    timings: List<Timing>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxHeight()) {
        timings.forEach { timing ->
            TimingCard(timing = timing)
        }
    }
}

@Composable
fun TimingCard(timing: Timing) {
    val topOffset = calculateOffset(timing.startTime)
    val bottomOffset = calculateOffset(timing.endTime)
    val height = bottomOffset - topOffset

    val containerColor = SacredTimelineColors.getTimingColor(timing)
    val contentColor = SacredTimelineColors.getContentColor(containerColor)

    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 1.dp)
            .offset(y = topOffset)
            .height(height - 2.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(1.dp, CardOuterBorderColor),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, CardBorderColor), RoundedCornerShape(4.dp))
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            if (height > 40.dp) {
                when (timing) {
                    is NallaNeram -> {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = contentColor.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    is SpecialPeriod -> {
                        val iconPainter = when (timing.name) {
                            "Rahu" -> painterResource(R.drawable.ic_rahu)
                            "Kuli Dawn", "Kuli Dusk" -> painterResource(R.drawable.ic_saturn)
                            else -> null
                        }

                        if (iconPainter != null) {
                            Icon(
                                painter = iconPainter,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = contentColor.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                    else -> {}
                }
            }

            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            Text(
                text = "${timing.startTime.format(timeFormatter)} - ${timing.endTime.format(timeFormatter)}",
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontSize = 9.sp,
                maxLines = 1
            )
            val label = when (timing) {
                is Hora -> timing.name
                is NallaNeram -> "Nalla"
                is GowriNeram -> timing.name
                is SpecialPeriod -> timing.name
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
}

private fun calculateOffset(time: LocalTime): Dp {
    val fraction = (time.hour - START_HOUR) + time.minute / 60f + time.second / 3600f
    return HOUR_HEIGHT * fraction
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun TimelineDashboardPreview() {
    SacredTimelineTheme {
        TimelineDashboard()
    }
}
