package com.suresh.sacredtimeline.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.TextAlign
import androidx.glance.unit.ColorProvider
import androidx.glance.ColorFilter
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.MainActivity
import com.suresh.sacredtimeline.data.CacheManager
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.logic.MockPanchangamProvider
import com.suresh.sacredtimeline.logic.SunriseSunsetProvider
import com.suresh.sacredtimeline.model.*
import com.suresh.sacredtimeline.ui.theme.*
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PanchangamWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = SettingsRepository(context)
        val cacheManager = CacheManager(context)
        val provider = MockPanchangamProvider()
        val sunProvider = SunriseSunsetProvider()
        
        val now = LocalTime.now()
        val date = LocalDate.now()

        // 1. Fetch Settings
        val mode = repository.locationMode.first()
        val is24Hour = repository.timeFormat24h.first()
        val columnVisibility = repository.widgetColumnVisibility.first()
        val columnOrder = repository.widgetColumnOrder.first()
        val lat: Double
        val lng: Double
        
        if (mode == "AUTO") {
            lat = 11.0168
            lng = 76.9558
        } else {
            lat = repository.manualLatitude.first()
            lng = repository.manualLongitude.first()
        }

        // 2. Fetch Data (Cache or Network)
        val cache = cacheManager.loadCache(lat, lng)
        val dayData: DayData = if (cache?.containsKey(date) == true) {
            cache[date]!!
        } else {
            val sunResult = sunProvider.getSunTimes(lat, lng, date)
            val timings = provider.getTimings(date, sunResult.sunrise, sunResult.sunset)
            DayData(
                nallaNeram = timings.filterIsInstance<NallaNeram>(),
                gowriNeram = timings.filterIsInstance<GowriNeram>(),
                hora = timings.filterIsInstance<Hora>(),
                specialPeriods = timings.filterIsInstance<SpecialPeriod>(),
                sunrise = sunResult.sunrise,
                sunset = sunResult.sunset,
                isFallback = sunResult.isFallback
            )
        }

        val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "hh:mm a")

        provideContent {
            GlanceTheme {
                WidgetContent(
                    dayData = dayData,
                    now = now,
                    columnVisibility = columnVisibility,
                    columnOrder = columnOrder,
                    timeFormatter = timeFormatter
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        dayData: DayData,
        now: LocalTime,
        columnVisibility: Set<String>,
        columnOrder: List<String>,
        timeFormatter: DateTimeFormatter
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .clickable(actionStartActivity<MainActivity>()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                columnOrder.forEach { colId ->
                    if (columnVisibility.contains(colId)) {
                        when (colId) {
                            "NERAM" -> {
                                val currentNalla = dayData.nallaNeram.find { it.isCurrent(now) }
                                val currentSpecial = dayData.specialPeriods.find { it.isCurrent(now) }
                                val nextNalla = dayData.nallaNeram.filter { it.startTime.isAfter(now) }.minByOrNull { it.startTime }
                                val nextSpecial = dayData.specialPeriods.filter { it.startTime.isAfter(now) }.minByOrNull { it.startTime }
                                
                                val timing = currentSpecial ?: currentNalla
                                val label = when {
                                    currentSpecial != null -> currentSpecial.name
                                    currentNalla != null -> "Nalla"
                                    else -> "None"
                                }
                                val next = if (nextSpecial != null && (nextNalla == null || nextSpecial.startTime.isBefore(nextNalla.startTime))) {
                                    nextSpecial
                                } else {
                                    nextNalla
                                }
                                
                                TimingColumn("Neram", label, timing, next, timeFormatter, GlanceModifier.defaultWeight())
                            }
                            "GOWRI" -> {
                                val currentGowri = dayData.gowriNeram.find { it.isCurrent(now) }
                                val nextGowri = dayData.gowriNeram.filter { it.startTime.isAfter(now) }.minByOrNull { it.startTime }
                                TimingColumn("Gowri", currentGowri?.name ?: "None", currentGowri, nextGowri, timeFormatter, GlanceModifier.defaultWeight())
                            }
                            "HORA" -> {
                                val currentHora = dayData.hora.find { it.isCurrent(now) }
                                val nextHora = dayData.hora.filter { it.startTime.isAfter(now) }.minByOrNull { it.startTime }
                                TimingColumn("Hora", currentHora?.name ?: "None", currentHora, nextHora, timeFormatter, GlanceModifier.defaultWeight())
                            }
                        }
                    }
                }
            }

            // Refresh Button Overlay
            Box(
                modifier = GlanceModifier.fillMaxSize().padding(8.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(28.dp)
                        .cornerRadius(14.dp)
                        .background(Color.White.copy(alpha = 0.5f))
                        .clickable(actionRunCallback<RefreshActionCallback>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_refresh_glance),
                        contentDescription = "Refresh",
                        modifier = GlanceModifier.size(18.dp),
                        colorFilter = ColorFilter.tint(ColorProvider(Color.Black))
                    )
                }
            }
        }
    }

    @Composable
    private fun TimingColumn(
        title: String,
        label: String,
        timing: Timing?,
        nextTiming: Timing?,
        timeFormatter: DateTimeFormatter,
        modifier: GlanceModifier = GlanceModifier
    ) {
        val backgroundColor = timing?.let { SacredTimelineColors.getTimingColor(it) } ?: Color.Gray
        val contentColor = SacredTimelineColors.getContentColor(backgroundColor)

        Box(
            modifier = modifier
                .fillMaxHeight()
                .padding(4.dp)
                .cornerRadius(8.dp)
                .background(CardOuterBorderColor),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .cornerRadius(7.dp)
                    .background(CardBorderColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(1.dp)
                        .cornerRadius(6.dp)
                        .background(backgroundColor),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(contentColor.copy(alpha = 0.8f))
                        )
                    )
                    
                    if (timing is SpecialPeriod && timing.name == "Yama") {
                        Image(
                            provider = ImageProvider(R.drawable.ic_yama_bull),
                            contentDescription = null,
                            modifier = GlanceModifier.size(32.dp)
                        )
                    }

                    Text(
                        text = label,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(contentColor)
                        )
                    )
                    timing?.tamilName?.let { tamil ->
                        Text(
                            text = tamil,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = ColorProvider(contentColor)
                            )
                        )
                    }

                    timing?.let {
                        Text(
                            text = "${it.startTime.format(timeFormatter)} - ${it.endTime.format(timeFormatter)}",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = ColorProvider(contentColor.copy(alpha = 0.9f))
                            )
                        )
                    }

                    // Next Upcoming Timing
                    nextTiming?.let { next ->
                        val nextName = if (next is NallaNeram) "Nalla" else next.name
                        Text(
                            text = "($nextName starts at ${next.startTime.format(timeFormatter)})",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = ColorProvider(contentColor.copy(alpha = 0.8f)),
                                textAlign = TextAlign.Center
                            ),
                            modifier = GlanceModifier.padding(top = 2.dp)
                        )
                    }
                }

                // Compatibility Icon for Hora
                if (timing is Hora) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize().padding(4.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        val (iconRes, color) = when (timing.compatibility) {
                            HoraCompatibility.FAVORABLE -> R.drawable.ic_fav to CompatibilityFavorable
                            HoraCompatibility.CONFLICTING -> R.drawable.ic_con to CompatibilityConflicting
                            HoraCompatibility.NEUTRAL -> R.drawable.ic_neu to CompatibilityNeutral
                        }
                        Box(
                            modifier = GlanceModifier
                                .size(24.dp)
                                .cornerRadius(12.dp)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                provider = ImageProvider(iconRes),
                                contentDescription = null,
                                modifier = GlanceModifier.size(16.dp),
                                colorFilter = ColorFilter.tint(ColorProvider(color))
                            )
                        }
                    }
                }
            }
        }
    }
}