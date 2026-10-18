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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.logic.DasaCalculator
import com.suresh.sacredtimeline.model.BirthProfile
import com.suresh.sacredtimeline.model.Metadata
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

    val context = LocalContext.current
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
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = stringResource(R.string.dasa_bhukti_title), style = MaterialTheme.typography.titleLarge)

                Text(
                    text = stringResource(R.string.active_prefix),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                val mahaName = Metadata.getPlanetName(dasaResult.currentMahadasa, context)
                val dasaLbl = stringResource(R.string.label_dasa)
                val bhuktiName = Metadata.getPlanetName(dasaResult.currentBhukti, context)
                val bhuktiLbl = stringResource(R.string.label_bhukti)
                val activeNameText = "$mahaName $dasaLbl — $bhuktiName $bhuktiLbl"

                Text(
                    text = activeNameText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(stringResource(R.string.tab_list_view)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(stringResource(R.string.tab_visual_track)) }
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
                                        Row(
                                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .background(getPlanetColor(dasa.planet), MaterialTheme.shapes.extraSmall)
                                            )
                                            val planetName = Metadata.getPlanetName(dasa.planet, context)
                                            val dasaLabel = stringResource(R.string.label_dasa)
                                            val yrsStr = String.format(Locale.US, "%.1f", dasa.totalYears)
                                            val dasaText = "$planetName $dasaLabel ($yrsStr yrs)"

                                            Text(
                                                text = dasaText,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isActiveDasa) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (isActiveDasa) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = MaterialTheme.shapes.extraSmall
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.badge_active),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
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

                                    Text(text = stringResource(R.string.bhuktis_sub_periods), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
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
                                                val bhuktiPlanet = Metadata.getPlanetName(bhukti.planet, context)
                                                val bhuktiLabel = stringResource(R.string.label_bhukti)
                                                val bhuktiText = "$bhuktiPlanet $bhuktiLabel"

                                                Text(
                                                    text = bhuktiText,
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
                Text(stringResource(R.string.btn_close))
            }
        }
    )
}

@Composable
fun DualLaneTimelineView(dasaResult: DasaCalculator.DasaResult) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val axisTextColor = if (isDark) Color.White else Color.DarkGray
    val gridLineColor = if (isDark) Color.LightGray.copy(alpha = 0.4f) else Color.LightGray
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

        var attempts = 0
        while ((if (isLandscape) horizontalScrollState.maxValue == 0 else verticalScrollState.maxValue == 0) && attempts < 15) {
            kotlinx.coroutines.delay(20)
            attempts++
        }

        if (isLandscape) {
            val totalWidth = 800.dp.value * density.density * effectiveScale
            val xNow = (((todayEpoch - startEpoch) / spanEpoch) * totalWidth)
            val centerOffset = 400.dp.value * density.density
            val max = horizontalScrollState.maxValue
            val targetScroll = (xNow - centerOffset).coerceIn(0.0, max.toDouble()).toInt()
            android.util.Log.d("scrollToNow", "landscape max=$max, target=$targetScroll")
            horizontalScrollState.scrollTo(targetScroll)
        } else {
            val totalHeight = 700.dp.value * effectiveScale * density.density
            val yNow = (30.0 + ((todayEpoch - startEpoch) / spanEpoch) * (totalHeight - 40f))
            val centerOffset = 225.dp.value * density.density
            val max = verticalScrollState.maxValue
            val targetScroll = (yNow - centerOffset).coerceIn(0.0, max.toDouble()).toInt()
            android.util.Log.d("scrollToNow", "portrait max=$max, target=$targetScroll")
            verticalScrollState.scrollTo(targetScroll)
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
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (isLandscape) stringResource(R.string.track_horizontal) else stringResource(R.string.track_vertical),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.span_format, birthDate.format(dateFormatter), endDate.format(dateFormatter)),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
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
                    Text(text = String.format(Locale.US, "%.1fx", trackScale), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { trackScale = (trackScale + 0.2f).coerceIn(0.1f, 15.0f) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Column Headers Row (Above Canvas)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.label_year_mon),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = axisTextColor,
                modifier = Modifier.width(90.dp)
            )
            Text(
                text = stringResource(R.string.label_mahadasa),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = axisTextColor,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.label_bhukti_heading),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = axisTextColor,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        if (isLandscape) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
                        val laneHeight = (h - 20f) / 2f
                        val yMahadasa = 10f
                        val yBhukti = yMahadasa + laneHeight + 10f

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
                                val planetLocalized = Metadata.getPlanetName(dasa.planet, context)
                                val textLayout = textMeasurer.measure(planetLocalized, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = fgColor))
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
                                    val bhuktiLocalized = Metadata.getPlanetName(bhukti.planet, context).take(3)
                                    val bText = textMeasurer.measure(bhuktiLocalized, style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = bFgColor))
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
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
                                val yearLayout = textMeasurer.measure(yearText, style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = axisTextColor))
                                drawText(yearLayout, topLeft = Offset(5f, yYearPos))
                                drawLine(gridLineColor, Offset(timeAxisWidth, yYearPos), Offset(w, yYearPos), 1.0f)

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
                                val planetLocalized = Metadata.getPlanetName(dasa.planet, context)
                                val textLayout = textMeasurer.measure(planetLocalized, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = fgColor))
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
                                    val bhuktiLocalized = Metadata.getPlanetName(bhukti.planet, context).take(3)
                                    val bText = textMeasurer.measure(bhuktiLocalized, style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = bFgColor))
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
