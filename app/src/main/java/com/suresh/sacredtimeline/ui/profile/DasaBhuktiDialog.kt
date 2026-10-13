package com.suresh.sacredtimeline.ui.profile

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.logic.DasaCalculator
import com.suresh.sacredtimeline.model.BirthProfile
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DasaBhuktiDialog(
    profile: BirthProfile,
    onDismiss: () -> Unit
) {
    val dasaResult = remember(profile) {
        DasaCalculator.calculateVimsottariDasa(profile)
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    var selectedTab by remember { mutableStateOf(0) } // 0 = List View, 1 = Visual Track
    val listState = rememberLazyListState()

    // Automatically scroll to the active Mahadasa when dialog opens in List View
    LaunchedEffect(dasaResult) {
        val activeIndex = dasaResult.allDasas.indexOfFirst { it.planet == dasaResult.currentMahadasa }
        if (activeIndex != -1) {
            listState.scrollToItem(activeIndex)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Vimsottari Dasa & Bhukti Timeline", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Active: ${dasaResult.currentMahadasa} Dasa — ${dasaResult.currentBhukti} Bhukti",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("List View") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Visual Track") }
                    )
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            ) {
                if (selectedTab == 0) {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(dasaResult.allDasas) { dasa ->
                            val isActiveDasa = dasa.planet == dasaResult.currentMahadasa
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isActiveDasa) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .background(getPlanetColor(dasa.planet), MaterialTheme.shapes.extraSmall)
                                            )
                                            Text(
                                                text = "${dasa.planet} Dasa (${String.format(Locale.US, "%.1f", dasa.totalYears)} yrs)",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isActiveDasa) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (isActiveDasa) {
                                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                                Text("Active", color = MaterialTheme.colorScheme.onPrimary)
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${dasa.startDate.format(dateFormatter)} — ${dasa.endDate.format(dateFormatter)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(text = "Bhuktis (Sub-periods):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    dasa.bhuktis.forEach { bhukti ->
                                        val isActiveBhukti = isActiveDasa && bhukti.planet == dasaResult.currentBhukti
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(getPlanetColor(bhukti.planet), MaterialTheme.shapes.extraSmall)
                                                )
                                                Text(
                                                    text = "${bhukti.planet} Bhukti",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = if (isActiveBhukti) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isActiveBhukti) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            Text(
                                                text = "${bhukti.startDate.format(dateFormatter)} to ${bhukti.endDate.format(dateFormatter)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isActiveBhukti) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isActiveBhukti) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Adaptive Dual-Lane Timeline Track with 3-letter Month Labels & Zoom
                    DualLaneTimelineView(dasaResult = dasaResult)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DualLaneTimelineView(dasaResult: DasaCalculator.DasaResult) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    val settingsRepo = remember { SettingsRepository(context) }
    val showNowLine by settingsRepo.showNowLine.collectAsState(initial = true)
    val nowLineColorInt by settingsRepo.nowLineColor.collectAsState(initial = 0xFF4CAF50.toInt())
    val nowLineThickness by settingsRepo.nowLineThickness.collectAsState(initial = 2.0f)
    val nowLineColor = Color(nowLineColorInt)
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var trackScale by remember { mutableStateOf(1.0f) }
    val effectiveScale = trackScale * 10.0f

    val textMeasurer = rememberTextMeasurer()
    val birthDate = dasaResult.allDasas.first().startDate
    val endDate = dasaResult.allDasas.last().endDate
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val scrollToNow: suspend () -> Unit = {
        val startEpoch = birthDate.toEpochDay().toDouble()
        val endEpoch = endDate.toEpochDay().toDouble()
        val spanEpoch = endEpoch - startEpoch
        val todayEpoch = LocalDate.now().toEpochDay().toDouble()
        val fraction = ((todayEpoch - startEpoch) / spanEpoch).coerceIn(0.0, 1.0)

        if (isLandscape) {
            val totalWidth = 800.dp.value * density.density * effectiveScale
            val targetScroll = (totalWidth * fraction - 200.0).coerceAtLeast(0.0)
            horizontalScrollState.animateScrollTo(targetScroll.toInt())
        } else {
            val totalHeight = 700.dp.value * effectiveScale * density.density
            val targetScroll = (totalHeight * fraction - 200.0).coerceAtLeast(0.0)
            verticalScrollState.animateScrollTo(targetScroll.toInt())
        }
    }

    LaunchedEffect(dasaResult, trackScale, isLandscape) {
        scrollToNow()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    trackScale = (trackScale * zoom).coerceIn(0.1f, 15.0f)
                }
            }
            .padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isLandscape) "Horizontal Track (Pinch to Zoom)" else "Vertical Track (Pinch to Zoom)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Span: ${birthDate.format(dateFormatter)} → ${endDate.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { scope.launch { scrollToNow() } },
                    modifier = Modifier.size(28.dp),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.Default.Today, contentDescription = "Jump to Now", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                }
                IconButton(onClick = { trackScale = (trackScale - 0.2f).coerceIn(0.1f, 15.0f) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(14.dp))
                }
                Text(text = String.format(Locale.US, "%.1fx", trackScale), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                IconButton(onClick = { trackScale = (trackScale + 0.2f).coerceIn(0.1f, 15.0f) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(14.dp))
                }
            }
        }

        if (isLandscape) {
            val horizontalScrollState = rememberScrollState()
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width((800.dp * effectiveScale).coerceAtLeast(800.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val laneHeight = (h - 50f) / 2f
                        val yMahadasa = 25f
                        val yBhukti = yMahadasa + laneHeight + 10f

                        val labelStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        drawText(textMeasurer.measure("Mahadasa", style = labelStyle), topLeft = Offset(10f, 5f))
                        drawText(textMeasurer.measure("Bhukti", style = labelStyle), topLeft = Offset(10f, yBhukti - 18f))

                        val startEpoch = birthDate.toEpochDay().toDouble()
                        val endEpoch = endDate.toEpochDay().toDouble()
                        val spanEpoch = endEpoch - startEpoch

                        dasaResult.allDasas.forEach { dasa ->
                            val dStart = dasa.startDate.toEpochDay().toDouble()
                            val dEnd = dasa.endDate.toEpochDay().toDouble()
                            val xStart = ((dStart - startEpoch) / spanEpoch) * w
                            val xEnd = ((dEnd - startEpoch) / spanEpoch) * w
                            val boxWidth = (xEnd - xStart).coerceAtLeast(4.0)

                            drawRect(
                                color = getPlanetColor(dasa.planet),
                                topLeft = Offset(xStart.toFloat(), yMahadasa),
                                size = Size(boxWidth.toFloat(), laneHeight)
                            )
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(xStart.toFloat(), yMahadasa),
                                size = Size(boxWidth.toFloat(), laneHeight),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.0f)
                            )

                            if (boxWidth > 40.0) {
                                val fgColor = getPlanetFgColor(dasa.planet)
                                val textLayout = textMeasurer.measure(dasa.planet, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = fgColor))
                                drawText(textLayout, topLeft = Offset(xStart.toFloat() + 4f, yMahadasa + laneHeight / 2f - 8f))
                            }

                            dasa.bhuktis.forEach { bhukti ->
                                val bStart = bhukti.startDate.toEpochDay().toDouble()
                                val bEnd = bhukti.endDate.toEpochDay().toDouble()
                                val bxStart = ((bStart - startEpoch) / spanEpoch) * w
                                val bxEnd = ((bEnd - startEpoch) / spanEpoch) * w
                                val bWidth = (bxEnd - bxStart).coerceAtLeast(2.0)

                                drawRect(
                                    color = getPlanetColor(bhukti.planet),
                                    topLeft = Offset(bxStart.toFloat(), yBhukti),
                                    size = Size(bWidth.toFloat(), laneHeight)
                                )
                                drawRect(
                                    color = Color.DarkGray,
                                    topLeft = Offset(bxStart.toFloat(), yBhukti),
                                    size = Size(bWidth.toFloat(), laneHeight),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.5f)
                                )

                                if (bWidth > 30.0) {
                                    val bFgColor = getPlanetFgColor(bhukti.planet)
                                    val bText = textMeasurer.measure(bhukti.planet.take(3), style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = bFgColor))
                                    drawText(bText, topLeft = Offset(bxStart.toFloat() + 2f, yBhukti + laneHeight / 2f - 6f))
                                }
                            }
                        }

                        if (showNowLine) {
                            val todayEpoch = LocalDate.now().toEpochDay().toDouble()
                            val xNow = (((todayEpoch - startEpoch) / spanEpoch) * w).toFloat()
                            if (xNow in 0f..w) {
                                drawLine(nowLineColor, Offset(xNow, 0f), Offset(xNow, h), nowLineThickness)
                                val nowLayout = textMeasurer.measure("NOW", style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = nowLineColor))
                                drawText(nowLayout, topLeft = Offset(xNow - 12f, h - 18f))
                            }
                        }
                    }
                }
            }
        } else {
            // Vertical Timeline for Portrait with 3-letter Month Abbreviations (Jan, Feb, Mar...)
            val verticalScrollState = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((700.dp * effectiveScale).coerceAtLeast(700.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val timeAxisWidth = 90f
                        val availableWidth = w - timeAxisWidth - 10f
                        val laneWidth = availableWidth / 2f
                        val xMahadasa = timeAxisWidth + 5f
                        val xBhukti = xMahadasa + laneWidth

                        val labelStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        drawText(textMeasurer.measure("Year / Mon", style = labelStyle), topLeft = Offset(5f, 2f))
                        drawText(textMeasurer.measure("Mahadasa", style = labelStyle), topLeft = Offset(xMahadasa, 2f))
                        drawText(textMeasurer.measure("Bhukti", style = labelStyle), topLeft = Offset(xBhukti, 2f))

                        val startEpoch = birthDate.toEpochDay().toDouble()
                        val endEpoch = endDate.toEpochDay().toDouble()
                        val spanEpoch = endEpoch - startEpoch

                        val startYear = birthDate.year
                        val endYear = endDate.year
                        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

                        var lastDrawnY = -50f
                        for (year in startYear..endYear) {
                            val yearStart = LocalDate.of(year, 1, 1)
                            val yearStartEpoch = yearStart.toEpochDay().toDouble()
                            val yYearPos = (30.0 + ((yearStartEpoch - startEpoch) / spanEpoch) * (h - 40f)).toFloat()

                            if (yYearPos >= 0f && yYearPos <= h && (yYearPos - lastDrawnY > 22f)) {
                                lastDrawnY = yYearPos
                                val yearText = "$year"
                                val yearLayout = textMeasurer.measure(yearText, style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black))
                                drawText(yearLayout, topLeft = Offset(5f, yYearPos))
                                drawLine(Color.LightGray, Offset(timeAxisWidth, yYearPos), Offset(w, yYearPos), 1.0f)

                                // Dynamically show 3-letter month abbreviations when zoomed in (trackScale >= 2.0f)
                                if (trackScale >= 2.0f) {
                                    val monthsToShow = if (trackScale >= 4.0f) (2..12) else listOf(4, 7, 10)
                                    monthsToShow.forEach { month ->
                                        val monthDate = LocalDate.of(year, month, 1)
                                        val monthEpoch = monthDate.toEpochDay().toDouble()
                                        val yMonthPos = (30.0 + ((monthEpoch - startEpoch) / spanEpoch) * (h - 40f)).toFloat()
                                        if (yMonthPos in 0f..h && (yMonthPos - lastDrawnY > 12f)) {
                                            val monthText = monthNames[month - 1]
                                            val monthLayout = textMeasurer.measure(monthText, style = TextStyle(fontSize = 8.sp, color = Color.Gray))
                                            drawText(monthLayout, topLeft = Offset(25f, yMonthPos - 4f))
                                            drawLine(Color.LightGray.copy(alpha = 0.3f), Offset(timeAxisWidth, yMonthPos), Offset(w, yMonthPos), 0.5f)
                                        }
                                    }
                                }
                            }
                        }

                        dasaResult.allDasas.forEach { dasa ->
                            val dStart = dasa.startDate.toEpochDay().toDouble()
                            val dEnd = dasa.endDate.toEpochDay().toDouble()
                            val yStart = 30.0 + ((dStart - startEpoch) / spanEpoch) * (h - 40f)
                            val yEnd = 30.0 + ((dEnd - startEpoch) / spanEpoch) * (h - 40f)
                            val boxHeight = (yEnd - yStart).coerceAtLeast(4.0)

                            val dasaColor = getPlanetColor(dasa.planet)
                            drawRect(
                                color = dasaColor,
                                topLeft = Offset(xMahadasa, yStart.toFloat()),
                                size = Size(laneWidth, boxHeight.toFloat())
                            )
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(xMahadasa, yStart.toFloat()),
                                size = Size(laneWidth, boxHeight.toFloat()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.0f)
                            )

                            if (boxHeight > 25.0) {
                                val fgColor = getPlanetFgColor(dasa.planet)
                                val textLayout = textMeasurer.measure(dasa.planet, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = fgColor))
                                drawText(textLayout, topLeft = Offset(xMahadasa + 8f, yStart.toFloat() + boxHeight.toFloat() / 2f - 6f))
                            }

                            dasa.bhuktis.forEach { bhukti ->
                                val bStart = bhukti.startDate.toEpochDay().toDouble()
                                val bEnd = bhukti.endDate.toEpochDay().toDouble()
                                val byStart = 30.0 + ((bStart - startEpoch) / spanEpoch) * (h - 40f)
                                val byEnd = 30.0 + ((bEnd - startEpoch) / spanEpoch) * (h - 40f)
                                val bHeight = (byEnd - byStart).coerceAtLeast(2.0)

                                val bhuktiColor = getPlanetColor(bhukti.planet)
                                drawRect(
                                    color = bhuktiColor,
                                    topLeft = Offset(xBhukti, byStart.toFloat()),
                                    size = Size(laneWidth, bHeight.toFloat())
                                )
                                drawRect(
                                    color = Color.DarkGray,
                                    topLeft = Offset(xBhukti, byStart.toFloat()),
                                    size = Size(laneWidth, bHeight.toFloat()),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.5f)
                                )

                                if (bHeight > 20.0) {
                                    val bFgColor = getPlanetFgColor(bhukti.planet)
                                    val bText = textMeasurer.measure(bhukti.planet.take(3), style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = bFgColor))
                                    drawText(bText, topLeft = Offset(xBhukti + 8f, byStart.toFloat() + bHeight.toFloat() / 2f - 5f))
                                }
                            }
                        }

                        if (showNowLine) {
                            val todayEpoch = LocalDate.now().toEpochDay().toDouble()
                            val yNow = (30.0 + ((todayEpoch - startEpoch) / spanEpoch) * (h - 40f)).toFloat()
                            if (yNow in 0f..h) {
                                drawLine(nowLineColor, Offset(0f, yNow), Offset(w, yNow), nowLineThickness)
                                val nowLayout = textMeasurer.measure("NOW", style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = nowLineColor))
                                drawText(nowLayout, topLeft = Offset(w - 35f, yNow - 14f))
                            }
                        }
                    }
                }
            }
        }
    }
}
