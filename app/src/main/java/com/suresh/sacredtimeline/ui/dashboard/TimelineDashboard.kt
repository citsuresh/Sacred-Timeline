package com.suresh.sacredtimeline.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineDashboard(
    modifier: Modifier = Modifier,
    viewModel: TimelineViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    
    var showDatePicker by remember { mutableStateOf(false) }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Sacred Timeline", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
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
            HorizontalDateDial(
                selectedDate = selectedDate,
                onDateSelected = { viewModel.updateDate(it) }
            )
            
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is TimelineUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is TimelineUiState.Success -> {
                        TimelineContent(state)
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalDateDial(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val dates = remember {
        (-30..30).map { LocalDate.now().plusDays(it.toLong()) }
    }
    val scrollState = rememberLazyListState()

    LaunchedEffect(selectedDate) {
        val index = dates.indexOf(selectedDate)
        if (index != -1) {
            scrollState.animateScrollToItem((index - 2).coerceAtLeast(0))
        }
    }

    LazyRow(
        state = scrollState,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
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
fun TimelineContent(state: TimelineUiState.Success) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    var containerHeight by remember { mutableStateOf(0) }
    
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(60000 - (System.currentTimeMillis() % 60000))
        }
    }
    
    // Auto-scroll to current time on first launch
    LaunchedEffect(containerHeight) {
        if (containerHeight > 0) {
            val nowOffset = with(density) { calculateOffset(currentTime).toPx() }
            val centerOffset = containerHeight / 2
            scrollState.scrollTo((nowOffset - centerOffset).toInt().coerceAtLeast(0))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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

                    Row(modifier = Modifier.fillMaxSize()) {
                        TimeMarkersColumn()
                        TimelineColumn(timings = state.nallaNeram, modifier = Modifier.weight(1f))
                        VerticalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        TimelineColumn(timings = state.gowriNeram, modifier = Modifier.weight(1f))
                        VerticalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        TimelineColumn(timings = state.hora, modifier = Modifier.weight(1f))
                    }

                    // Dynamic Current Time Indicator (inside scrollable area)
                    NowIndicator(currentTime)
                }
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
            .padding(end = 8.dp)
            .align(Alignment.TopEnd)
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
fun TimelineHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp)
    ) {
        Spacer(modifier = Modifier.width(TIME_COLUMN_WIDTH))
        Text("Nalla Neram", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
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
                    color = Color.Gray,
                    fontSize = 10.sp
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

    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .offset(y = topOffset)
            .height(height)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = timing.auspiciousness.toColor().copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            Text(
                text = "${timing.startTime.format(timeFormatter)} - ${timing.endTime.format(timeFormatter)}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontSize = 9.sp,
                maxLines = 1
            )
            val label = when (timing) {
                is Hora -> timing.name
                is NallaNeram -> "Nalla"
                is GowriNeram -> timing.name
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

private fun Auspiciousness.toColor(): Color = when (this) {
    Auspiciousness.GREEN -> AuspiciousGreen
    Auspiciousness.BLUE -> AuspiciousBlue
    Auspiciousness.RED -> InauspiciousRed
    Auspiciousness.AMBER -> CautionAmber
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
