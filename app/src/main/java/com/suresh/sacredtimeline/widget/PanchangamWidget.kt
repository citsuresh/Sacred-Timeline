package com.suresh.sacredtimeline.widget

import android.content.Context
import android.content.res.Configuration
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
import com.suresh.sacredtimeline.logic.DayDataProvider
import com.suresh.sacredtimeline.model.*
import com.suresh.sacredtimeline.ui.theme.*
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class PanchangamWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = SettingsRepository(context)
        val cacheManager = CacheManager(context)
        
        val now = LocalTime.now()
        val date = LocalDate.now()

        // 1. Fetch Settings
        val language = repository.language.first()
        val localizedContext = context.createConfigurationContext(
            Configuration(context.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(language))
            }
        )
        
        val mode = repository.locationMode.first()
        val is24Hour = repository.timeFormat24h.first()
        val columnVisibility = repository.widgetColumnVisibility.first()
        val columnOrder = repository.widgetColumnOrder.first()
        
        val lat: Double
        val lng: Double
        
        if (mode == "AUTO") {
            lat = repository.lastKnownLatitude.first()
            lng = repository.lastKnownLongitude.first()
        } else {
            lat = repository.manualLatitude.first()
            lng = repository.manualLongitude.first()
        }

        // 2. Fetch Data (Cache or Network)
        val cache = cacheManager.loadCache(lat, lng)
        val dayData: DayData = if (cache?.containsKey(date) == true) {
            cache[date]!!
        } else {
            val data = DayDataProvider(context).fetchDayData(date, lat, lng)
            
            // Fix: Cache Write-Back
            val newCache = cache?.toMutableMap() ?: mutableMapOf()
            newCache[date] = data
            cacheManager.saveCache(lat, lng, newCache)
            data
        }

        val timeFormatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "hh:mm a")

        provideContent {
            GlanceTheme {
                WidgetContent(
                    context = localizedContext,
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
        context: Context,
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
                            "UNIVERSAL" -> {
                                UniversalTimingColumn(
                                    context = context,
                                    dayData = dayData,
                                    now = now,
                                    timeFormatter = timeFormatter,
                                    modifier = GlanceModifier.defaultWeight()
                                )
                            }
                            "NERAM_MUHURTHAM" -> {
                                val currentNalla = dayData.nallaNeram.find { it.isCurrent(now) }
                                val currentSpecial = dayData.specialPeriods.find { it.isCurrent(now) }
                                val currentAbhijit = if (dayData.abhijitMuhurtham?.isCurrent(now) == true) dayData.abhijitMuhurtham else null
                                val currentBrahma = if (dayData.brahmaMuhurtham?.isCurrent(now) == true) dayData.brahmaMuhurtham else null
                                val currentMaitra = dayData.maitraMuhurtham.find { it.isCurrent(now) }
                                
                                val timing = currentMaitra ?: currentSpecial ?: currentAbhijit ?: currentBrahma ?: currentNalla
                                val labelRes = when {
                                    currentMaitra != null -> R.string.timing_maitra
                                    currentSpecial != null -> Metadata.getSpecialNameRes(currentSpecial.name)
                                    currentAbhijit != null -> R.string.muhurtham_abhijit
                                    currentBrahma != null -> R.string.muhurtham_brahma
                                    currentNalla != null -> Metadata.getSpecialNameRes("Nalla")
                                    else -> R.string.nav_nalla_neram
                                }
                                val label = if (timing != null) context.getString(labelRes) else "None"
                                
                                // Simplified next logic
                                val next = (dayData.nallaNeram + dayData.specialPeriods + 
                                            dayData.maitraMuhurtham +
                                            listOfNotNull(dayData.brahmaMuhurtham, dayData.abhijitMuhurtham))
                                            .filter { it.startTime.isAfter(now) }
                                            .minByOrNull { it.startTime }

                                TimingColumn(context, context.getString(R.string.nav_neram_muhurtham), label, timing, next, timeFormatter, GlanceModifier.defaultWeight())
                            }
                            "ABHIJIT" -> {
                                val currentAbhijit = if (dayData.abhijitMuhurtham?.isCurrent(now) == true) dayData.abhijitMuhurtham else null
                                val nextAbhijit = if (dayData.abhijitMuhurtham?.startTime?.isAfter(now) == true) dayData.abhijitMuhurtham else null
                                val label = if (currentAbhijit != null) context.getString(R.string.muhurtham_abhijit) else "None"
                                TimingColumn(context, context.getString(R.string.muhurtham_abhijit), label, currentAbhijit, nextAbhijit, timeFormatter, GlanceModifier.defaultWeight())
                            }
                            "NERAM" -> {
                                val currentNalla = dayData.nallaNeram.find { it.isCurrent(now) }
                                val currentSpecial = dayData.specialPeriods.find { it.isCurrent(now) }
                                val nextNalla = dayData.nallaNeram.filter { it.startTime.isAfter(now) }.minByOrNull { it.startTime }
                                val nextSpecial = dayData.specialPeriods.filter { it.startTime.isAfter(now) }.minByOrNull { it.startTime }
                                
                                val timing = currentSpecial ?: currentNalla
                                val labelRes = when {
                                    currentSpecial != null -> Metadata.getSpecialNameRes(currentSpecial.name)
                                    currentNalla != null -> Metadata.getSpecialNameRes("Nalla")
                                    else -> R.string.app_name // Placeholder
                                }
                                val label = if (timing != null) context.getString(labelRes) else "None"
                                
                                val next = if (nextSpecial != null && (nextNalla == null || nextSpecial.startTime.isBefore(nextNalla.startTime))) {
                                    nextSpecial
                                } else {
                                    nextNalla
                                }
                                
                                TimingColumn(context, context.getString(R.string.nav_nalla_neram), label, timing, next, timeFormatter, GlanceModifier.defaultWeight())
                            }
                            "GOWRI" -> {
                                val currentGowri = dayData.gowriNeram.find { it.isCurrent(now) }
                                val nextGowri = dayData.gowriNeram.filter { it.startTime.isAfter(now) }.minByOrNull { it.startTime }
                                val label = currentGowri?.let { context.getString(Metadata.getGowriNameRes(it.name)) } ?: "None"
                                TimingColumn(context, context.getString(R.string.nav_gowri_neram), label, currentGowri, nextGowri, timeFormatter, GlanceModifier.defaultWeight())
                            }
                            "HORA" -> {
                                val currentHora = dayData.hora.find { it.isCurrent(now) }
                                val nextHora = dayData.hora.filter { it.startTime.isAfter(now) }.minByOrNull { it.startTime }
                                val label = currentHora?.let { context.getString(Metadata.getPlanetNameRes(it.name)) } ?: "None"
                                TimingColumn(context, context.getString(R.string.nav_hora), label, currentHora, nextHora, timeFormatter, GlanceModifier.defaultWeight())
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
    private fun UniversalTimingColumn(
        context: Context,
        dayData: DayData,
        now: LocalTime,
        timeFormatter: DateTimeFormatter,
        modifier: GlanceModifier = GlanceModifier
    ) {
        val currentGowri = dayData.gowriNeram.find { it.isCurrent(now) }
        val currentHora = dayData.hora.find { it.isCurrent(now) }
        val currentNalla = dayData.nallaNeram.find { it.isCurrent(now) }
        val currentSpecial = dayData.specialPeriods.find { it.isCurrent(now) }
        val currentAbhijit = if (dayData.abhijitMuhurtham?.isCurrent(now) == true) dayData.abhijitMuhurtham else null
        val currentBrahma = if (dayData.brahmaMuhurtham?.isCurrent(now) == true) dayData.brahmaMuhurtham else null

        val middleTiming = currentSpecial ?: currentAbhijit ?: currentBrahma ?: currentNalla

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
                        .background(Color.White), // Use white for high density universal view
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = context.getString(R.string.nav_universal),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(Color.Black.copy(alpha = 0.6f))
                        )
                    )
                    
                    Spacer(modifier = GlanceModifier.height(4.dp))

                    // Dynamic Lane Arrangement (Matches App Logic)
                    Row(
                        modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 4.dp).defaultWeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val currentMaitra = dayData.maitraMuhurtham.find { it.isCurrent(now) }
                        val activeLanes = buildList {
                            if (currentGowri != null) add("GOWRI")
                            if (middleTiming != null || currentMaitra != null) add("NERAM")
                            if (currentHora != null) add("HORAI")
                        }

                        activeLanes.forEachIndexed { index, laneId ->
                            when (laneId) {
                                "GOWRI" -> {
                                    UniversalMiniLane(
                                        title = context.getString(R.string.nav_gowri_neram),
                                        timing = currentGowri,
                                        context = context,
                                        labelProvider = { Metadata.getGowriNameRes(it.name) },
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                }
                                "NERAM" -> {
                                    val finalMiddleTiming = currentMaitra ?: middleTiming
                                    UniversalMiniLane(
                                        title = context.getString(if (currentMaitra != null) R.string.view_mode_maitra else R.string.label_neram_short),
                                        timing = finalMiddleTiming,
                                        context = context,
                                        labelProvider = { 
                                            when (it) {
                                                is MaitraMuhurtham -> R.string.timing_maitra
                                                is SpecialPeriod -> Metadata.getSpecialNameRes(it.name)
                                                is Muhurtham -> if (it.name.contains("Abhijit")) R.string.muhurtham_abhijit else R.string.muhurtham_brahma
                                                else -> Metadata.getSpecialNameRes("Nalla")
                                            }
                                        },
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                }
                                "HORAI" -> {
                                    UniversalMiniLane(
                                        title = context.getString(R.string.nav_hora),
                                        timing = currentHora,
                                        context = context,
                                        labelProvider = { Metadata.getPlanetNameRes(it.name) },
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                }
                            }
                            if (index < activeLanes.size - 1) {
                                Spacer(modifier = GlanceModifier.width(2.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun UniversalMiniLane(
        title: String,
        timing: Timing?,
        context: Context,
        labelProvider: (Timing) -> Int,
        modifier: GlanceModifier = GlanceModifier
    ) {
        val color = timing?.let { SacredTimelineColors.getTimingColor(it) } ?: Color.LightGray.copy(alpha = 0.3f)
        val contentColor = if (timing != null) SacredTimelineColors.getContentColor(color) else Color.Gray
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        Box(
            modifier = modifier
                .fillMaxHeight()
                .padding(1.dp)
                .background(color)
                .cornerRadius(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = TextStyle(
                        fontSize = 9.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = ColorProvider(contentColor.copy(alpha = 0.9f)),
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    modifier = GlanceModifier.padding(top = 2.dp)
                )
                
                Spacer(modifier = GlanceModifier.defaultWeight())

                Text(
                    text = if (timing != null) context.getString(labelProvider(timing)) else "None",
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorProvider(contentColor), textAlign = TextAlign.Center),
                    maxLines = 1
                )
                
                if (timing is MaitraMuhurtham) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_fav), // Star icon
                            contentDescription = null,
                            modifier = GlanceModifier.size(8.dp),
                            colorFilter = ColorFilter.tint(ColorProvider(contentColor))
                        )
                        Spacer(modifier = GlanceModifier.width(2.dp))
                        Text(
                            text = "${timing.potencyStars}*",
                            style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Medium, color = ColorProvider(contentColor)),
                        )
                    }
                }

                if (timing != null) {
                    Text(
                        text = context.getString(R.string.label_till, timing.endTime.format(timeFormatter)),
                        style = TextStyle(fontSize = 7.sp, color = ColorProvider(contentColor.copy(alpha = 0.8f))),
                        maxLines = 1
                    )
                }

                Spacer(modifier = GlanceModifier.defaultWeight())
            }

            // Compatibility Icon for Hora in Universal View
            if (timing is Hora) {
                Box(
                    modifier = GlanceModifier.fillMaxSize().padding(2.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    val (iconRes, tintColor) = when (timing.compatibility) {
                        HoraCompatibility.FAVORABLE -> R.drawable.ic_fav to CompatibilityFavorable
                        HoraCompatibility.CONFLICTING -> R.drawable.ic_con to CompatibilityConflicting
                        HoraCompatibility.NEUTRAL -> R.drawable.ic_neu to CompatibilityNeutral
                    }
                    Box(
                        modifier = GlanceModifier
                            .size(16.dp) // Reduced from 24dp to prevent title overlap
                            .cornerRadius(8.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            provider = ImageProvider(iconRes),
                            contentDescription = null,
                            modifier = GlanceModifier.size(12.dp), // Adjusted accordingly
                            colorFilter = ColorFilter.tint(ColorProvider(tintColor))
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun TimingColumn(
        context: Context,
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

                    timing?.let {
                        Text(
                            text = "${it.startTime.format(timeFormatter)} - ${it.endTime.format(timeFormatter)}",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = ColorProvider(contentColor.copy(alpha = 0.9f))
                            )
                        )
                    }

                    if (timing is MaitraMuhurtham) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = GlanceModifier.padding(top = 4.dp)) {
                            for (i in 1..timing.potencyStars) {
                                Image(
                                    provider = ImageProvider(R.drawable.ic_fav),
                                    contentDescription = null,
                                    modifier = GlanceModifier.size(10.dp),
                                    colorFilter = ColorFilter.tint(ColorProvider(contentColor))
                                )
                            }
                        }
                        Text(
                            text = context.getString(Metadata.getMaitraPotencyRes(timing.potencyStars)),
                            style = TextStyle(fontSize = 9.sp, color = ColorProvider(contentColor.copy(alpha = 0.8f))),
                            modifier = GlanceModifier.padding(top = 2.dp)
                        )
                    }

                    // Next Upcoming Timing
                    nextTiming?.let { next ->
                        Text(
                            text = "(${next.startTime.format(timeFormatter)})",
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
