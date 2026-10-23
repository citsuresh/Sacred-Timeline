package com.suresh.sacredtimeline.ui.profile

import android.content.Context
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.model.BirthProfile
import com.suresh.sacredtimeline.model.ChartStyle
import com.suresh.sacredtimeline.model.LunarMonthSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class AddressSuggestion(
    val displayName: String,
    val latitude: Double,
    val longitude: Double
)

object GeoHelper {
    fun getSuggestions(context: Context, query: String): List<AddressSuggestion> {
        if (query.length < 3) return emptyList()
        try {
            if (Geocoder.isPresent()) {
                // Force Locale.ENGLISH so autocomplete suggestions are always stored in standard English script
                val geocoder = Geocoder(context, Locale.ENGLISH)
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 6)
                if (!addresses.isNullOrEmpty()) {
                    return addresses.mapNotNull { addr ->
                        val parts = listOfNotNull(addr.locality ?: addr.subAdminArea, addr.adminArea, addr.countryName)
                        val name = if (parts.isNotEmpty()) parts.joinToString(", ") else addr.getAddressLine(0)
                        if (name != null) {
                            AddressSuggestion(
                                displayName = name,
                                latitude = addr.latitude,
                                longitude = addr.longitude
                            )
                        } else null
                    }.distinctBy { it.displayName }
                }
            }
        } catch (_: Exception) {}
        return emptyList()
    }

    fun resolveCoordinates(context: Context, placeName: String): Pair<Double, Double>? {
        if (placeName.isBlank()) return null
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.ENGLISH)
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(placeName, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    return Pair(address.latitude, address.longitude)
                }
            }
        } catch (_: Exception) {}
        return null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAddDialog(
    initialProfile: BirthProfile? = null,
    onDismiss: () -> Unit,
    onSave: (BirthProfile) -> Unit
) {
    val context = LocalContext.current
    val settingsRepo = remember { SettingsRepository(context) }
    val is24Hour by settingsRepo.timeFormat24h.collectAsState(initial = false)
    val dateFormat by settingsRepo.dateFormat.collectAsState(initial = "DD-MM-YYYY")

    val defaultDateStr = remember(dateFormat, initialProfile) {
        val rawDate = initialProfile?.birthDate ?: "1995-06-15"
        try {
            val parsed = if (rawDate.contains("-")) {
                val parts = rawDate.split("-")
                if (parts[0].length == 4) {
                    LocalDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
                } else {
                    LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                }
            } else {
                LocalDate.of(1995, 6, 15)
            }
            val pattern = when (dateFormat) {
                "MM-DD-YYYY" -> "MM-dd-yyyy"
                "YYYY-MM-DD" -> "yyyy-MM-dd"
                else -> "dd-MM-yyyy"
            }
            parsed.format(DateTimeFormatter.ofPattern(pattern))
        } catch (_: Exception) {
            rawDate
        }
    }

    var nameEnglish by remember { mutableStateOf(initialProfile?.nameEnglish ?: "") }
    var nameRegional by remember { mutableStateOf(initialProfile?.nameRegional ?: "") }
    var birthDate by remember { mutableStateOf(defaultDateStr) }
    var birthTime by remember { mutableStateOf(initialProfile?.birthTime ?: if (is24Hour) "06:30" else "06:30 AM") }
    var birthPlace by remember { mutableStateOf(initialProfile?.birthPlace ?: "") }
    var latitude by remember { mutableStateOf(initialProfile?.latitude?.toString() ?: "") }
    var longitude by remember { mutableStateOf(initialProfile?.longitude?.toString() ?: "") }
    var chartStyle by remember { mutableStateOf(initialProfile?.chartStyle ?: ChartStyle.SOUTH_INDIAN) }
    var lunarSystem by remember { mutableStateOf(initialProfile?.lunarSystem ?: LunarMonthSystem.AMANTA) }

    var photoUri by remember { mutableStateOf(initialProfile?.photoUri ?: "") }
    var avatarColor by remember { mutableStateOf(initialProfile?.avatarColor ?: 0xFF6750A4.toInt()) }
    var initialsField by remember { mutableStateOf(initialProfile?.initials ?: "") }
    var userModifiedInitials by remember { mutableStateOf(initialProfile != null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { photoUri = it.toString() }
    }

    fun computeInitials(name: String): String {
        return name.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .take(2)
            .joinToString("")
    }

    LaunchedEffect(nameEnglish) {
        if (!userModifiedInitials && photoUri.isBlank()) {
            initialsField = computeInitials(nameEnglish)
        }
    }

    var placeExpanded by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    var geoStatusText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<AddressSuggestion>>(emptyList()) }

    // Debounced live global Geocoder autocomplete search as user types (always in English)
    LaunchedEffect(birthPlace) {
        if (birthPlace.length >= 3 && placeExpanded) {
            isSearching = true
            delay(400)
            withContext(Dispatchers.IO) {
                val results = GeoHelper.getSuggestions(context, birthPlace)
                withContext(Dispatchers.Main) {
                    suggestions = results
                    isSearching = false
                }
            }
        } else {
            suggestions = emptyList()
        }
    }

    val isNameValid = nameEnglish.isNotBlank()
    val isDateValid = birthDate.matches(Regex("\\d{2}-\\d{2}-\\d{4}")) || birthDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))
    val isTimeValid = if (is24Hour) {
        birthTime.matches(Regex("\\d{2}:\\d{2}"))
    } else {
        birthTime.matches(Regex("\\d{1,2}:\\d{2}\\s*(AM|PM|am|pm)?"))
    }
    val isPlaceValid = birthPlace.isNotBlank() && latitude.isNotBlank() && longitude.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (initialProfile == null) stringResource(R.string.add_profile) else stringResource(R.string.edit_profile)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Mandatory fields are marked with *. Place names are saved in standard English format for cross-language consistency.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Profile Avatar & Color Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileAvatarBadge(
                        profile = BirthProfile(
                            nameEnglish = nameEnglish.ifBlank { "User" },
                            birthDate = "2000-01-01",
                            birthTime = "06:00",
                            birthPlace = "",
                            latitude = 0.0,
                            longitude = 0.0,
                            photoUri = photoUri.ifBlank { null },
                            avatarColor = avatarColor,
                            initials = initialsField
                        ),
                        size = 56.dp
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedButton(
                            onClick = { photoPickerLauncher.launch("image/*") }
                        ) {
                            Text(if (photoUri.isBlank()) stringResource(R.string.select_photo) else stringResource(R.string.change_photo))
                        }
                        if (photoUri.isNotBlank()) {
                            TextButton(onClick = { photoUri = "" }) {
                                Text(stringResource(R.string.remove_photo), color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = stringResource(R.string.avatar_bg_color), style = MaterialTheme.typography.labelMedium)
                val palette = listOf(
                    0xFF6750A4.toInt(), 0xFF1976D2.toInt(), 0xFF009688.toInt(),
                    0xFF4CAF50.toInt(), 0xFFFF9800.toInt(), 0xFFE53935.toInt(),
                    0xFF3F51B5.toInt(), 0xFFE91E63.toInt(), 0xFF00BCD4.toInt(), 0xFFFF5722.toInt()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    palette.forEach { colorInt ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(colorInt))
                                .clickable { avatarColor = colorInt },
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarColor == colorInt) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (photoUri.isBlank()) {
                    OutlinedTextField(
                        value = initialsField,
                        onValueChange = { 
                            initialsField = it
                            userModifiedInitials = true
                        },
                        label = { Text(stringResource(R.string.initials_label)) },
                        placeholder = { Text(stringResource(R.string.initials_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Mandatory Field: English Name
                OutlinedTextField(
                    value = nameEnglish,
                    onValueChange = { nameEnglish = it },
                    label = { Text("${stringResource(R.string.name_english)} *") },
                    isError = showError && !isNameValid,
                    supportingText = {
                        if (showError && !isNameValid) {
                            Text(stringResource(R.string.error_name_required), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Optional Field: Regional Name
                OutlinedTextField(
                    value = nameRegional,
                    onValueChange = { nameRegional = it },
                    label = { Text("${stringResource(R.string.name_regional)} (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mandatory Field: Birth Date
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    label = { Text("${stringResource(R.string.birth_date)} ($dateFormat) *") },
                    isError = showError && !isDateValid,
                    supportingText = {
                        if (showError && !isDateValid) {
                            Text(stringResource(R.string.error_format_must_be, dateFormat), color = MaterialTheme.colorScheme.error)
                        } else {
                            Text(stringResource(R.string.format_label, dateFormat))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mandatory Field: Birth Time
                OutlinedTextField(
                    value = birthTime,
                    onValueChange = { birthTime = it },
                    label = { Text(stringResource(R.string.birth_time_label, if (is24Hour) "HH:MM (24-hour)" else "hh:MM AM/PM")) },
                    isError = showError && !isTimeValid,
                    supportingText = {
                        if (showError && !isTimeValid) {
                            Text(stringResource(R.string.error_format_must_be, if (is24Hour) "HH:MM (24-hour)" else "hh:MM AM/PM"), color = MaterialTheme.colorScheme.error)
                        } else {
                            Text(stringResource(R.string.format_label, if (is24Hour) "HH:MM (24-hour)" else "hh:MM AM/PM"))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mandatory Field: Birth Place with English Geocoder Autocomplete Dropdown
                ExposedDropdownMenuBox(
                    expanded = placeExpanded && suggestions.isNotEmpty(),
                    onExpandedChange = { placeExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = birthPlace,
                        onValueChange = { 
                            birthPlace = it
                            placeExpanded = true
                            geoStatusText = ""
                        },
                        label = { Text("${stringResource(R.string.birth_place)} *") },
                        isError = showError && !isPlaceValid,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = placeExpanded && suggestions.isNotEmpty()) },
                        supportingText = {
                            if (showError && !isPlaceValid) {
                                Text(stringResource(R.string.error_birth_place_required), color = MaterialTheme.colorScheme.error)
                            } else if (geoStatusText.isNotEmpty()) {
                                Text(geoStatusText, color = if (geoStatusText.contains("✓")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            } else {
                                Text(if (isSearching) stringResource(R.string.searching_locations) else stringResource(R.string.type_for_suggestions))
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = placeExpanded && suggestions.isNotEmpty(),
                        onDismissRequest = { placeExpanded = false }
                    ) {
                        suggestions.forEach { suggestion ->
                            DropdownMenuItem(
                                text = { Text(suggestion.displayName) },
                                onClick = {
                                    birthPlace = suggestion.displayName
                                    latitude = suggestion.latitude.toString()
                                    longitude = suggestion.longitude.toString()
                                    placeExpanded = false
                                    geoStatusText = context.getString(R.string.coordinates_resolved)
                                }
                            )
                        }
                    }
                }

                // Optional Fields: Latitude / Longitude
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text(stringResource(R.string.latitude_label)) },
                        placeholder = { Text(stringResource(R.string.required_placeholder)) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text(stringResource(R.string.longitude_label)) },
                        placeholder = { Text(stringResource(R.string.required_placeholder)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(text = stringResource(R.string.chart_style), style = MaterialTheme.typography.bodyMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = chartStyle == ChartStyle.SOUTH_INDIAN,
                        onClick = { chartStyle = ChartStyle.SOUTH_INDIAN },
                        label = { Text(stringResource(R.string.south_indian)) }
                    )
                    FilterChip(
                        selected = chartStyle == ChartStyle.NORTH_INDIAN,
                        onClick = { chartStyle = ChartStyle.NORTH_INDIAN },
                        label = { Text(stringResource(R.string.north_indian)) }
                    )
                }

                Text(text = stringResource(R.string.lunar_month_system_label), style = MaterialTheme.typography.bodyMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = lunarSystem == LunarMonthSystem.AMANTA,
                        onClick = { lunarSystem = LunarMonthSystem.AMANTA },
                        label = { Text(stringResource(R.string.amanta_system)) }
                    )
                    FilterChip(
                        selected = lunarSystem == LunarMonthSystem.PURNIMANTA,
                        onClick = { lunarSystem = LunarMonthSystem.PURNIMANTA },
                        label = { Text(stringResource(R.string.purnimanta_system)) }
                    )
                }
            }
        },
        confirmButton = {
            val saveScope = rememberCoroutineScope()
            Button(
                onClick = {
                    val finalLat = latitude.toDoubleOrNull()
                    val finalLon = longitude.toDoubleOrNull()
                    val isLatLonValid = finalLat != null && finalLon != null

                    if (isNameValid && isDateValid && isTimeValid && isPlaceValid && isLatLonValid) {
                        saveScope.launch(Dispatchers.IO) {
                            val normalizedDate = try {
                                val parts = birthDate.split("-")
                                when (dateFormat) {
                                    "MM-DD-YYYY" -> "${parts[2]}-${parts[0]}-${parts[1]}"
                                    "YYYY-MM-DD" -> birthDate
                                    else -> "${parts[2]}-${parts[1]}-${parts[0]}"
                                }
                            } catch (_: Exception) {
                                birthDate
                            }

                            val profile = BirthProfile(
                                id = initialProfile?.id ?: java.util.UUID.randomUUID().toString(),
                                nameEnglish = nameEnglish,
                                nameRegional = nameRegional,
                                birthDate = normalizedDate,
                                birthTime = birthTime,
                                birthPlace = birthPlace,
                                latitude = finalLat!!,
                                longitude = finalLon!!,
                                timezoneOffsetHours = 5.5,
                                chartStyle = chartStyle,
                                lunarSystem = lunarSystem,
                                isPrimary = initialProfile?.isPrimary ?: false,
                                photoUri = photoUri.ifBlank { null },
                                avatarColor = avatarColor,
                                initials = initialsField
                            )
                            withContext(Dispatchers.Main) {
                                onSave(profile)
                            }
                        }
                    } else {
                        showError = true
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
