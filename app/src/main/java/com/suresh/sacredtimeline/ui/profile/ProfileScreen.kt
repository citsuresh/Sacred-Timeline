package com.suresh.sacredtimeline.ui.profile

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.data.BirthProfilesRepository
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.logic.BirthChartCalculator
import com.suresh.sacredtimeline.model.BirthProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: BirthProfilesRepository,
    onBack: () -> Unit
) {
    var profiles by remember { mutableStateOf<List<BirthProfile>>(emptyList()) }
    var selectedProfile by remember { mutableStateOf<BirthProfile?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var profileToEdit by remember { mutableStateOf<BirthProfile?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val settingsRepo = remember { SettingsRepository(context) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val loaded = repository.getAllProfiles()
            val activeId = settingsRepo.activeProfileId.first()
            val primary = loaded.find { it.isPrimary } ?: loaded.firstOrNull()
            val initial = loaded.find { it.id == activeId } ?: primary
            withContext(Dispatchers.Main) {
                profiles = loaded
                selectedProfile = initial
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Person, contentDescription = "Back")
                    }
                },
                actions = {
                    selectedProfile?.let { profile ->
                        IconButton(onClick = { profileToEdit = profile }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_profile))
                        }
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_profile))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Ultra-Compact Minimalist Profile Selector Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
                    .clickable { dropdownExpanded = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stringResource(R.string.active_profile)}:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedProfile?.let { "${it.nameEnglish}${if (it.isPrimary) " • ${stringResource(R.string.label_primary)}" else ""}" } ?: stringResource(R.string.select_profile),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    profiles.forEach { profile ->
                        DropdownMenuItem(
                            text = { 
                                Text("${profile.nameEnglish}${if (profile.isPrimary) " • ${stringResource(R.string.label_primary)}" else ""}") 
                            },
                            onClick = {
                                selectedProfile = profile
                                dropdownExpanded = false
                                scope.launch(Dispatchers.IO) {
                                    repository.setPrimary(profile.id)
                                    val updated = repository.getAllProfiles()
                                    withContext(Dispatchers.Main) {
                                        profiles = updated
                                    }
                                }
                            }
                        )
                    }
                }
            }

            HorizontalDivider()

            // Main Details Panel for the Selected Profile
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileDetailsContent(
                    profile = selectedProfile,
                    repository = repository,
                    onEditClick = { profileToEdit = it },
                    onProfilesUpdated = { updated ->
                        profiles = updated
                        selectedProfile = updated.find { it.isPrimary } ?: updated.firstOrNull()
                    }
                )
            }
        }
    }

    if (showAddDialog || profileToEdit != null) {
        ProfileAddDialog(
            initialProfile = profileToEdit,
            onDismiss = { 
                showAddDialog = false
                profileToEdit = null
            },
            onSave = { newProfile ->
                scope.launch(Dispatchers.IO) {
                    repository.addOrUpdateProfile(newProfile)
                    val updated = repository.getAllProfiles()
                    withContext(Dispatchers.Main) {
                        profiles = updated
                        selectedProfile = updated.find { it.id == newProfile.id } ?: updated.firstOrNull()
                        showAddDialog = false
                        profileToEdit = null
                    }
                }
            }
        )
    }
}

@Composable
fun ProfileDetailsContent(
    profile: BirthProfile?,
    repository: BirthProfilesRepository,
    onEditClick: (BirthProfile) -> Unit,
    onProfilesUpdated: (List<BirthProfile>) -> Unit
) {
    val context = LocalContext.current
    val settingsRepo = remember { SettingsRepository(context) }
    val is24Hour by settingsRepo.timeFormat24h.collectAsState(initial = false)
    val dateFormat by settingsRepo.dateFormat.collectAsState(initial = "DD-MM-YYYY")
    val scope = rememberCoroutineScope()
    var showDasaDialog by remember { mutableStateOf(false) }

    profile?.let { p ->
        // Calculate actual real astrological chart based on user's birth profile
        val chartResult = remember(p) {
            BirthChartCalculator.calculateChart(p)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Header Row: Profile Avatar + Name + Edit & Delete Icon Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileAvatarBadge(profile = p, size = 44.dp)
                        Column {
                            Text(text = p.nameEnglish, style = MaterialTheme.typography.titleLarge)
                            if (p.nameRegional.isNotBlank()) {
                                Text(text = p.nameRegional, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { onEditClick(p) }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_profile), tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    repository.removeProfile(p.id)
                                    val updated = repository.getAllProfiles()
                                    withContext(Dispatchers.Main) {
                                        onProfilesUpdated(updated)
                                    }
                                }
                            },
                            enabled = repository.getAllProfiles().size > 1
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.btn_delete), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                HorizontalDivider()

                // Stacked Birth Details
                val formattedDate = formatDisplayDate(p.birthDate, dateFormat)
                val formattedTime = formatDisplayTime(p.birthTime, is24Hour)

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = stringResource(R.string.birth_date), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(text = formattedDate, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = stringResource(R.string.birth_time), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(text = formattedTime, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = stringResource(R.string.birth_place), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(text = p.birthPlace, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))

                // Explicit Summary of Lagna & Moon Rasi in Profile Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.label_lagna), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Text(text = getLocalizedRasiFullName(context, chartResult.lagnaHouse), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.label_moon_rasi), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Text(text = getLocalizedRasiFullName(context, chartResult.rasiHouse), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }

                if (!p.isPrimary) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                repository.setPrimary(p.id)
                                com.suresh.sacredtimeline.data.CacheManager(context).clearCache()
                                com.suresh.sacredtimeline.worker.WidgetUpdateWorker.triggerImmediateUpdate(context)
                                val updated = repository.getAllProfiles()
                                withContext(Dispatchers.Main) {
                                    onProfilesUpdated(updated)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.set_primary_profile))
                    }
                }
            }
        }

        // Macro Insights Card with Clickable Dasa & Bhukti row & Info icon
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = stringResource(R.string.macro_insights), style = MaterialTheme.typography.titleMedium)
                
                // Clickable Active Dasa & Bhukti row with Info icon
                Card(
                    onClick = { showDasaDialog = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = stringResource(R.string.active_dasa_bhukti), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(text = stringResource(R.string.default_dasa_bhukti), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Default.Info, contentDescription = "View Vimsottari Dasa Timeline", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                // Dynamic Jupiter Transit House from Moon Rasi (Janma Rasi)
                val jupiterRasi = chartResult.rasiPositions["Jupiter"] ?: 1
                val jupiterHouse = ((jupiterRasi - chartResult.rasiHouse + 12) % 12) + 1
                val jupiterVerdictRes = getTransitVerdictRes("Jupiter", jupiterHouse)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = stringResource(R.string.label_jupiter_transit), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(text = "${stringResource(getHouseTitleRes(jupiterHouse))} — ${stringResource(jupiterVerdictRes)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(text = stringResource(getHouseGuidanceRes(jupiterHouse)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Dynamic Saturn Transit House from Moon Rasi (Janma Rasi)
                val saturnRasi = chartResult.rasiPositions["Saturn"] ?: 1
                val saturnHouse = ((saturnRasi - chartResult.rasiHouse + 12) % 12) + 1
                val saturnVerdictRes = getTransitVerdictRes("Saturn", saturnHouse)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = stringResource(R.string.label_saturn_transit), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(text = "${stringResource(getHouseTitleRes(saturnHouse))} — ${stringResource(saturnVerdictRes)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(text = stringResource(getHouseGuidanceRes(saturnHouse)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        AstroChartCanvas(
            planetaryPositions = chartResult.rasiPositions,
            chartStyle = p.chartStyle,
            lagnaHouse = chartResult.lagnaHouse,
            rasiHouse = chartResult.rasiHouse,
            title = stringResource(R.string.rasi_chart)
        )

        AstroChartCanvas(
            planetaryPositions = chartResult.navamsaPositions,
            chartStyle = p.chartStyle,
            lagnaHouse = chartResult.lagnaHouse,
            rasiHouse = chartResult.rasiHouse,
            title = stringResource(R.string.navamsa_chart)
        )
    } ?: run {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_profile_selected))
        }
    }

    if (showDasaDialog && profile != null) {
        DasaBhuktiDialog(
            profile = profile,
            onDismiss = { showDasaDialog = false }
        )
    }
}

private fun getLocalizedRasiFullName(context: android.content.Context, h: Int): String {
    val resId = when (h) {
        1 -> R.string.rasi_full_1
        2 -> R.string.rasi_full_2
        3 -> R.string.rasi_full_3
        4 -> R.string.rasi_full_4
        5 -> R.string.rasi_full_5
        6 -> R.string.rasi_full_6
        7 -> R.string.rasi_full_7
        8 -> R.string.rasi_full_8
        9 -> R.string.rasi_full_9
        10 -> R.string.rasi_full_10
        11 -> R.string.rasi_full_11
        12 -> R.string.rasi_full_12
        else -> 0
    }
    return if (resId != 0) context.getString(resId) else "$h"
}

private fun getTransitVerdictRes(planet: String, house: Int): Int {
    return when (planet) {
        "Jupiter" -> {
            when (house) {
                2, 5, 7, 9, 11 -> R.string.transit_verdict_auspicious
                else -> R.string.transit_verdict_challenging
            }
        }
        "Saturn" -> {
            when (house) {
                3, 6, 11 -> R.string.transit_verdict_favorable
                else -> R.string.transit_verdict_challenging
            }
        }
        else -> R.string.transit_verdict_neutral
    }
}

private fun getHouseTitleRes(house: Int): Int = when(house) {
    1 -> R.string.house_1_title
    2 -> R.string.house_2_title
    3 -> R.string.house_3_title
    4 -> R.string.house_4_title
    5 -> R.string.house_5_title
    6 -> R.string.house_6_title
    7 -> R.string.house_7_title
    8 -> R.string.house_8_title
    9 -> R.string.house_9_title
    10 -> R.string.house_10_title
    11 -> R.string.house_11_title
    12 -> R.string.house_12_title
    else -> R.string.house_1_title
}

private fun getHouseGuidanceRes(house: Int): Int = when(house) {
    1 -> R.string.house_1_guidance
    2 -> R.string.house_2_guidance
    3 -> R.string.house_3_guidance
    4 -> R.string.house_4_guidance
    5 -> R.string.house_5_guidance
    6 -> R.string.house_6_guidance
    7 -> R.string.house_7_guidance
    8 -> R.string.house_8_guidance
    9 -> R.string.house_9_guidance
    10 -> R.string.house_10_guidance
    11 -> R.string.house_11_guidance
    12 -> R.string.house_12_guidance
    else -> R.string.house_1_guidance
}

private fun formatDisplayDate(dateStr: String, dateFormat: String): String {
    return try {
        val parsedDate = if (dateStr.contains("-")) {
            val parts = dateStr.split("-")
            if (parts[0].length == 4) {
                LocalDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            } else {
                LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
            }
        } else {
            LocalDate.parse(dateStr)
        }
        val pattern = when (dateFormat) {
            "MM-DD-YYYY" -> "MM-dd-yyyy"
            "YYYY-MM-DD" -> "yyyy-MM-dd"
            else -> "dd-MM-yyyy"
        }
        parsedDate.format(DateTimeFormatter.ofPattern(pattern))
    } catch (e: Exception) {
        dateStr
    }
}

private fun formatDisplayTime(timeStr: String, is24Hour: Boolean): String {
    return try {
        val parsedTime = try {
            LocalTime.parse(timeStr)
        } catch (e: Exception) {
            try {
                LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.US))
            } catch (e2: Exception) {
                null
            }
        }
        if (parsedTime != null) {
            val formatter = DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "hh:mm a")
            parsedTime.format(formatter)
        } else {
            timeStr
        }
    } catch (e: Exception) {
        timeStr
    }
}

val avatarPalette = listOf(
    androidx.compose.ui.graphics.Color(0xFF6750A4), // Purple
    androidx.compose.ui.graphics.Color(0xFF1976D2), // Blue
    androidx.compose.ui.graphics.Color(0xFF009688), // Teal
    androidx.compose.ui.graphics.Color(0xFF4CAF50), // Green
    androidx.compose.ui.graphics.Color(0xFFFF9800), // Amber
    androidx.compose.ui.graphics.Color(0xFFE53935), // Red
    androidx.compose.ui.graphics.Color(0xFF3F51B5), // Indigo
    androidx.compose.ui.graphics.Color(0xFFE91E63), // Pink
    androidx.compose.ui.graphics.Color(0xFF00BCD4), // Cyan
    androidx.compose.ui.graphics.Color(0xFFFF5722)  // Deep Orange
)

@Composable
fun ProfileAvatarBadge(
    profile: BirthProfile?,
    size: androidx.compose.ui.unit.Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    val name = profile?.nameEnglish ?: "User"
    val initials = when {
        profile?.initials != null -> profile.initials
        else -> {
            name.split(" ")
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .take(2)
                .joinToString("")
                .ifEmpty { "U" }
        }
    }
    
    val bgColor = if (profile != null && profile.avatarColor != 0) {
        androidx.compose.ui.graphics.Color(profile.avatarColor)
    } else {
        val hash = kotlin.math.abs((profile?.id ?: "default").hashCode() + name.hashCode())
        avatarPalette[hash % avatarPalette.size]
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (!profile?.photoUri.isNullOrBlank()) {
            coil.compose.AsyncImage(
                model = profile?.photoUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(androidx.compose.foundation.shape.CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else if (initials.isNotEmpty()) {
            Text(
                text = initials,
                style = MaterialTheme.typography.labelSmall,
                color = androidx.compose.ui.graphics.Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.38f).sp
            )
        }
    }
}
