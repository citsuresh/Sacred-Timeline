package com.suresh.sacredtimeline.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
        while (!curr.isAfter(end)) {
            val holidayResIds = VerifiedHolidays.getHolidays(curr)
            holidayResIds.forEach { resId ->
                val title = try { context.getString(resId) } catch (_: Exception) { "Holiday" }
                list.add(SearchResult(curr, title, "Holidays"))
            }
            if (VerifiedHolidays.isSubhaMuhurtham(curr)) {
                list.add(SearchResult(curr, subhaMuhurthamTitle, "Muhurthams", isMuhurtham = true))
            }
            curr = curr.plusDays(1)
        }
        list
    }

    val categories = listOf("All", "Holidays", "Muhurthams")

    val filteredEvents = remember(searchQuery, selectedCategory, allEvents) {
        allEvents.filter { event ->
            val matchesCategory = selectedCategory == "All" || event.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() || event.title.contains(searchQuery, ignoreCase = true) || event.date.toString().contains(searchQuery)
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

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDateSelected(event.date) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = event.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = localizedCategory,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = event.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
