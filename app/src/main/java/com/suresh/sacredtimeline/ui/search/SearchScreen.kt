package com.suresh.sacredtimeline.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.data.RemindersRepository
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.data.VerifiedHolidays
import com.suresh.sacredtimeline.logic.LunarCalendarUtils
import com.suresh.sacredtimeline.logic.PanchangamCalculator
import com.suresh.sacredtimeline.logic.SunriseSunsetProvider
import com.suresh.sacredtimeline.logic.TamilCalendarUtils
import com.suresh.sacredtimeline.ui.dashboard.components.MoonPhaseIcon
import com.suresh.sacredtimeline.ui.theme.HolidayPale
import com.suresh.sacredtimeline.ui.theme.HolidayPurple
import com.suresh.sacredtimeline.ui.theme.SubhaMuhurthamGold
import com.suresh.sacredtimeline.ui.theme.SubhaMuhurthamPale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class SearchResult(
    val date: LocalDate,
    val title: String,
    val category: String,
    val isMuhurtham: Boolean = false,
    val isCustom: Boolean = false,
    val isStar: Boolean = false,
    val isTithi: Boolean = false,
    val tithiValue: Int = 1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var allEvents by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var displayedLimit by remember(searchQuery, selectedCategory) { mutableStateOf(20) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val subhaMuhurthamTitle = stringResource(R.string.subha_muhurtham_title)

    LaunchedEffect(subhaMuhurthamTitle, context) {
        withContext(Dispatchers.IO) {
            val list = mutableListOf<SearchResult>()
            val start = LocalDate.now()
            val end = LocalDate.now().plusDays(60)
            var curr = start
            val defaultLat = 13.0827
            val defaultLng = 80.2707
            val sunProvider = SunriseSunsetProvider()
            val settingsRepo = SettingsRepository(context)
            val enabledStars = settingsRepo.enabledNakshatras.first()
            val enabledTithis = settingsRepo.enabledTithis.first()

            val nakshatraNamesList = listOf(
                "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra", "Punarvasu", "Pushya", "Ashlesha",
                "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha",
                "Mula", "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
            ).map { "$it Nakshatra" }

            val tithiNamesList = listOf(
                "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami", "Shashti", "Saptami", "Ashtami", "Navami", "Dashami",
                "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Pournami", "Amavasai"
            ).map { "$it Tithi" }

            var lastStarValue = -1
            var lastTithiValue = -1

            while (!curr.isAfter(end)) {
                val holidayResIds = VerifiedHolidays.getHolidays(curr)
                holidayResIds.forEach { resId ->
                    val title = try { context.getString(resId) } catch (_: Exception) { "Holiday" }
                    list.add(SearchResult(curr, title, "Holidays"))
                }
                if (VerifiedHolidays.isSubhaMuhurtham(curr)) {
                    list.add(SearchResult(curr, subhaMuhurthamTitle, "Muhurthams", isMuhurtham = true))
                }

                // Calculate Maitra Muhurthams
                try {
                    val sunTimes = sunProvider.getSunTimes(defaultLat, defaultLng, curr)
                    val maitras = PanchangamCalculator.calculateMaitraMuhurtham(curr, defaultLat, defaultLng, sunTimes.sunrise)
                    maitras.forEach { maitra ->
                        val timeStr = "${maitra.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))} - ${maitra.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
                        list.add(SearchResult(curr, "Maitra Muhurtham ($timeStr)", "Muhurthams", isMuhurtham = true))
                    }
                } catch (_: Exception) {}

                // Index user-selected Nakshatras and Tithis (Start of event occurrence only, strict Tithi ID matching)
                try {
                    val lunarInfo = LunarCalendarUtils.getLunarDayInfo(curr, "AMANTA")
                    lunarInfo.nakshatras.forEach { interval ->
                        if (enabledStars.contains("STAR_${interval.value}")) {
                            if (interval.value != lastStarValue) {
                                lastStarValue = interval.value
                                val starName = try { context.getString(interval.resId) } catch (_: Exception) { "Nakshatra" }
                                list.add(SearchResult(curr, starName, "Nakshatras", isStar = true))
                            }
                        }
                    }
                    lunarInfo.tithis.forEach { interval ->
                        if (enabledTithis.contains("TITHI_${interval.value}")) {
                            if (interval.value != lastTithiValue) {
                                lastTithiValue = interval.value
                                val tithiName = try { context.getString(interval.resId) } catch (_: Exception) { "Tithi" }
                                list.add(SearchResult(curr, tithiName, "Tithis", isTithi = true, tithiValue = interval.value))
                            }
                        }
                    }
                } catch (_: Exception) {}

                curr = curr.plusDays(1)
            }

            // Add user custom reminders
            try {
                val customReminders = RemindersRepository(context).getAllReminders()
                customReminders.forEach { reminder ->
                    list.add(SearchResult(reminder.eventDate, reminder.eventTitle, "Custom Events", isCustom = true))
                }
            } catch (_: Exception) {}

            // Deduplicate identical entries
            val distinctList = list.distinctBy { Triple(it.date, it.title, it.category) }.toMutableList()

            // Sort chronologically by date
            distinctList.sortBy { it.date }

            withContext(Dispatchers.Main) {
                allEvents = distinctList
                isLoading = false
            }
        }
    }

    val categories = listOf("All", "Holidays", "Muhurthams", "Custom Events", "Nakshatras", "Tithis")

    val filteredEvents = remember(searchQuery, selectedCategory, allEvents) {
        allEvents.filter { event ->
            val matchesCategory = selectedCategory == "All" || event.category == selectedCategory
            val matchesQuery = if (searchQuery.isBlank()) {
                // Default view: show current or future events (today onwards)
                !event.date.isBefore(LocalDate.now())
            } else {
                // Search query entered: search across all dates in the window
                event.title.contains(searchQuery, ignoreCase = true) || event.date.toString().contains(searchQuery)
            }
            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.search_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.label_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.label_clear))
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = {
                            val labelText = when(cat) {
                                "All" -> stringResource(R.string.category_all)
                                "Holidays" -> stringResource(R.string.category_holidays)
                                "Muhurthams" -> stringResource(R.string.category_muhurtham)
                                "Custom Events" -> stringResource(R.string.category_custom_events)
                                "Nakshatras" -> stringResource(R.string.category_nakshatras)
                                "Tithis" -> stringResource(R.string.category_tithis)
                                else -> cat
                            }
                            Text(labelText)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredEvents.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_results),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredEvents.take(displayedLimit)) { event ->
                        val localizedCategory = when(event.category) {
                            "Holidays" -> stringResource(R.string.category_holidays)
                            "Muhurthams" -> stringResource(R.string.category_muhurtham)
                            "Custom Events" -> stringResource(R.string.category_custom_events)
                            "Nakshatras" -> stringResource(R.string.category_nakshatras)
                            "Tithis" -> stringResource(R.string.category_tithis)
                            else -> event.category
                        }
                        val tamilDate = remember(event.date) { TamilCalendarUtils.getTamilDate(event.date) }
                        val tamilMonth = stringResource(tamilDate.monthResId)
                        val tamilYear = stringResource(tamilDate.yearResId)

                        val iconTint = when {
                            event.isMuhurtham -> SubhaMuhurthamGold
                            event.isCustom -> MaterialTheme.colorScheme.primary
                            event.isStar -> MaterialTheme.colorScheme.tertiary
                            event.isTithi -> MaterialTheme.colorScheme.secondary
                            else -> HolidayPurple
                        }
                        val cardBg = when {
                            event.isMuhurtham -> SubhaMuhurthamPale
                            event.isCustom -> MaterialTheme.colorScheme.surfaceVariant
                            event.isStar -> MaterialTheme.colorScheme.surfaceVariant
                            event.isTithi -> MaterialTheme.colorScheme.surfaceVariant
                            else -> HolidayPale
                        }
                        val borderColor = when {
                            event.isMuhurtham -> SubhaMuhurthamGold.copy(alpha = 0.5f)
                            event.isCustom -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            event.isStar -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
                            event.isTithi -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                            else -> HolidayPurple.copy(alpha = 0.4f)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDateSelected(event.date) },
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = iconTint.copy(alpha = 0.2f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (event.isTithi) {
                                            MoonPhaseIcon(
                                                tithi = event.tithiValue,
                                                modifier = Modifier.size(28.dp),
                                                lightColor = Color.White, // Pure white full moon
                                                darkColor = Color(0xFF263238),   // Dark new moon
                                                strokeColor = iconTint
                                            )
                                        } else {
                                            val icon = when {
                                                event.isMuhurtham -> Icons.Default.AutoAwesome
                                                event.isCustom -> Icons.Default.Event
                                                event.isStar -> Icons.Default.Star
                                                else -> Icons.Default.Celebration
                                            }
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = iconTint,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    // 1. English Date
                                    Text(
                                        text = event.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = iconTint
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    // 2. Tamil Date
                                    Text(
                                        text = "${tamilDate.day} $tamilMonth, $tamilYear",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    // 3. Event Title & Category
                                    Text(
                                        text = event.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = localizedCategory,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (filteredEvents.size > displayedLimit) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                OutlinedButton(
                                    onClick = { displayedLimit += 20 }
                                ) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Load More")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
