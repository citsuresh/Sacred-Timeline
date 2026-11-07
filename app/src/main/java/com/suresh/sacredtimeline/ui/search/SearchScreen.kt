package com.suresh.sacredtimeline.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.data.VerifiedHolidays
import com.suresh.sacredtimeline.logic.PanchangamCalculator
import com.suresh.sacredtimeline.logic.SunriseSunsetProvider
import com.suresh.sacredtimeline.logic.TamilCalendarUtils
import com.suresh.sacredtimeline.ui.theme.HolidayPale
import com.suresh.sacredtimeline.ui.theme.HolidayPurple
import com.suresh.sacredtimeline.ui.theme.SubhaMuhurthamGold
import com.suresh.sacredtimeline.ui.theme.SubhaMuhurthamPale
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class SearchResult(
    val date: LocalDate,
    val title: String,
    val category: String,
    val isMuhurtham: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val context = androidx.compose.ui.platform.LocalContext.current

    val subhaMuhurthamTitle = stringResource(R.string.subha_muhurtham_title)

    val allEvents = remember(subhaMuhurthamTitle) {
        val list = mutableListOf<SearchResult>()
        val start = LocalDate.of(2026, 1, 1)
        val end = LocalDate.of(2026, 12, 31)
        var curr = start
        val defaultLat = 13.0827
        val defaultLng = 80.2707
        val sunProvider = SunriseSunsetProvider()

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

            curr = curr.plusDays(1)
        }
        list
    }

    val categories = listOf("All", "Holidays", "Muhurthams")

    val filteredEvents = remember(searchQuery, selectedCategory, allEvents) {
        allEvents.filter { event ->
            val matchesCategory = selectedCategory == "All" || event.category == selectedCategory
            val matchesQuery = if (searchQuery.isBlank()) {
                // Default view: show current or future events (today onwards)
                !event.date.isBefore(LocalDate.now())
            } else {
                // Search query entered: search across all dates in the year
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
                modifier = Modifier.fillMaxWidth(),
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
                                else -> cat
                            }
                            Text(labelText)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredEvents.isEmpty()) {
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
                    items(filteredEvents) { event ->
                        val localizedCategory = when(event.category) {
                            "Holidays" -> stringResource(R.string.category_holidays)
                            "Muhurthams" -> stringResource(R.string.category_muhurtham)
                            else -> event.category
                        }
                        val tamilDate = remember(event.date) { TamilCalendarUtils.getTamilDate(event.date) }
                        val tamilMonth = stringResource(tamilDate.monthResId)
                        val tamilYear = stringResource(tamilDate.yearResId)

                        val icon = if (event.isMuhurtham) Icons.Default.AutoAwesome else Icons.Default.Celebration
                        val iconTint = if (event.isMuhurtham) SubhaMuhurthamGold else HolidayPurple
                        val cardBg = if (event.isMuhurtham) SubhaMuhurthamPale else HolidayPale
                        val borderColor = if (event.isMuhurtham) SubhaMuhurthamGold.copy(alpha = 0.5f) else HolidayPurple.copy(alpha = 0.4f)

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
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = iconTint,
                                            modifier = Modifier.size(24.dp)
                                        )
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
                }
            }
        }
    }
}
