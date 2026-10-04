package com.suresh.sacredtimeline.ui.dashboard.components

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.data.RemindersRepository
import com.suresh.sacredtimeline.logic.LunarCalendarUtils
import com.suresh.sacredtimeline.logic.TamilCalendarUtils
import com.suresh.sacredtimeline.model.Reminder
import com.suresh.sacredtimeline.model.ReminderType
import com.suresh.sacredtimeline.worker.ReminderWorker
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReminderDialog(
    defaultTitle: String = "",
    defaultCategory: String = "UNIVERSAL",
    defaultDate: LocalDate = LocalDate.now(),
    defaultStartTime: LocalTime = LocalTime.of(6, 0),
    defaultEndTime: LocalTime = LocalTime.of(18, 0),
    existingReminder: Reminder? = null,
    onDismiss: () -> Unit,
    onSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState("android.permission.POST_NOTIFICATIONS")
    } else {
        null
    }
    
    LaunchedEffect(Unit) {
        if (permissionState != null && !permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    var eventTitle by remember { mutableStateOf(existingReminder?.eventTitle ?: defaultTitle) }
    var selectedCategory by remember { mutableStateOf(existingReminder?.eventCategory ?: defaultCategory) }
    
    val nakshatraNames = listOf(
        "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra", "Punarvasu", "Pushya", "Ashlesha",
        "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha",
        "Mula", "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
    ).map { "$it Nakshatra" }

    val muhurthamNames = listOf("Brahma Muhurtham", "Abhijit Muhurtham", "Maitra Muhurtham", "Subha Muhurtham")
    val neramNames = listOf("Nalla Neram", "Rahu Kalam", "Yamagandam", "Kuligai")
    val horaNames = listOf("Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn").map { "$it Horai" }
    val gowriNames = listOf("Amridha", "Uthi", "Labam", "Dhanam", "Sugam", "Soram", "Visham", "Rogam").map { "$it Gowri Neram" }
    val tithiNames = listOf(
        "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami", "Shashti", "Saptami", "Ashtami", "Navami", "Dashami",
        "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Pournami", "Amavasai"
    ).map { "$it Tithi" }
    val chandrashtamamNames = listOf("Mesham", "Rishabham", "Midhunam", "Kadagam", "Simmam", "Kanni", "Thulam", "Viruchigam", "Dhanusu", "Magaram", "Kumbam", "Meenam").map { "Chandrashtamam ($it)" }
    val tamilFestivalNames = listOf(
        "1st of Every Tamil Month",
        "Tamil New Year (Chithirai 1)",
        "Thai Pongal",
        "Deepavali",
        "Aadi 18 (Aadi Perukku)",
        "Karthigai Deepam",
        "Vinayagar Chaturthi",
        "Pradosham",
        "Mahashivaratri"
    )
    val yogamNames = listOf("Amrita Yoga", "Siddha Yoga", "Marana Yoga")
    val tharabalamNames = listOf("Jenma Tharai", "Selva Tharai", "Vipathu Tharai", "Suga Tharai", "Pagai Tharai")
    val solarNames = listOf("Sunrise", "Sunset")

    val currentItems = when (selectedCategory.uppercase()) {
        "NAKSHATRA" -> nakshatraNames
        "MUHURTHAM" -> muhurthamNames
        "NERAM" -> neramNames
        "HORA" -> horaNames
        "GOWRI" -> gowriNames
        "TITHI" -> tithiNames
        "CHANDRASHTAMAM" -> chandrashtamamNames
        "TAMIL_FESTIVAL" -> tamilFestivalNames
        "SOLAR" -> solarNames
        "YOGAM" -> yogamNames
        "THARA_BALAM" -> tharabalamNames
        else -> emptyList()
    }

    fun calculateNextDate(cat: String, title: String): LocalDate {
        val today = LocalDate.now()
        when (cat.uppercase()) {
            "NAKSHATRA" -> {
                val starIdx = nakshatraNames.indexOf(title) + 1
                if (starIdx in 1..27) {
                    for (i in 0..35) {
                        val testDate = today.plusDays(i.toLong())
                        try {
                            val info = LunarCalendarUtils.getLunarDayInfo(testDate, "AMANTA")
                            if (info.nakshatras.any { it.value == starIdx }) return testDate
                        } catch (_: Exception) {}
                    }
                }
            }
            "TITHI" -> {
                val tithiIdx = tithiNames.indexOf(title) + 1
                if (tithiIdx in 1..30) {
                    for (i in 0..20) {
                        val testDate = today.plusDays(i.toLong())
                        try {
                            val info = LunarCalendarUtils.getLunarDayInfo(testDate, "AMANTA")
                            if (info.tithis.any { it.value == tithiIdx }) return testDate
                        } catch (_: Exception) {}
                    }
                }
            }
            "TAMIL_FESTIVAL" -> {
                if (title.contains("1st of Every Tamil Month")) {
                    for (i in 0..35) {
                        val testDate = today.plusDays(i.toLong())
                        try {
                            if (TamilCalendarUtils.getTamilDate(testDate).day == 1) return testDate
                        } catch (_: Exception) {}
                    }
                }
            }
            else -> {}
        }
        return today
    }

    var customTimeStr by remember { mutableStateOf("06:00") }
    var offsetDays by remember { mutableStateOf(existingReminder?.offsetDays?.toString() ?: "0") }
    var offsetHours by remember { mutableStateOf(existingReminder?.offsetHours?.toString() ?: "0") }
    var offsetMinutes by remember { mutableStateOf(existingReminder?.offsetMinutes?.toString() ?: "15") }
    var selectedType by remember { mutableStateOf(existingReminder?.reminderType ?: ReminderType.START_RELATIVE) }
    var isRecurring by remember { mutableStateOf(existingReminder?.isRecurring ?: false) }

    var expandedCategory by remember { mutableStateOf(false) }
    val categories = listOf("NAKSHATRA", "TITHI", "CHANDRASHTAMAM", "YOGAM", "THARA_BALAM", "MUHURTHAM", "NERAM", "HORA", "GOWRI", "TAMIL_FESTIVAL", "SOLAR", "CUSTOM", "GENERAL")

    var expandedItem by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingReminder == null) stringResource(R.string.add_reminder) else stringResource(R.string.edit_reminder)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedCategory = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${stringResource(R.string.category_label)}: $selectedCategory")
                    }
                    DropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    expandedCategory = false
                                    eventTitle = when (cat.uppercase()) {
                                        "NAKSHATRA" -> nakshatraNames.firstOrNull() ?: ""
                                        "MUHURTHAM" -> muhurthamNames.firstOrNull() ?: ""
                                        "NERAM" -> neramNames.firstOrNull() ?: ""
                                        "HORA" -> horaNames.firstOrNull() ?: ""
                                        "GOWRI" -> gowriNames.firstOrNull() ?: ""
                                        "TITHI" -> tithiNames.firstOrNull() ?: ""
                                        "CHANDRASHTAMAM" -> chandrashtamamNames.firstOrNull() ?: ""
                                        "TAMIL_FESTIVAL" -> tamilFestivalNames.firstOrNull() ?: ""
                                        "SOLAR" -> solarNames.firstOrNull() ?: ""
                                        "YOGAM" -> yogamNames.firstOrNull() ?: ""
                                        "THARA_BALAM" -> tharabalamNames.firstOrNull() ?: ""
                                        else -> ""
                                    }
                                }
                            )
                        }
                    }
                }

                if (selectedCategory.uppercase() == "CUSTOM" || selectedCategory.uppercase() == "GENERAL") {
                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text(stringResource(R.string.event_title_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expandedItem = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Event: $eventTitle")
                        }
                        DropdownMenu(
                            expanded = expandedItem,
                            onDismissRequest = { expandedItem = false }
                        ) {
                            currentItems.forEach { itemName ->
                                DropdownMenuItem(
                                    text = { Text(itemName) },
                                    onClick = {
                                        eventTitle = itemName
                                        expandedItem = false
                                    }
                                )
                            }
                        }
                    }
                }

                Text(stringResource(R.string.frequency), style = MaterialTheme.typography.bodyMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isRecurring,
                        onClick = { isRecurring = false },
                        label = { Text(stringResource(R.string.single_occurrence)) }
                    )
                    FilterChip(
                        selected = isRecurring,
                        onClick = { isRecurring = true },
                        label = { Text(stringResource(R.string.all_occurrences)) }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.reminder_strategy), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = selectedType == ReminderType.START_RELATIVE,
                            onClick = { selectedType = ReminderType.START_RELATIVE },
                            label = { Text(stringResource(R.string.relative_to_start)) }
                        )
                        FilterChip(
                            selected = selectedType == ReminderType.END_RELATIVE,
                            onClick = { selectedType = ReminderType.END_RELATIVE },
                            label = { Text(stringResource(R.string.relative_to_end)) }
                        )
                    }
                    FilterChip(
                        selected = selectedType == ReminderType.CUSTOM_ABSOLUTE,
                        onClick = { selectedType = ReminderType.CUSTOM_ABSOLUTE },
                        label = { Text(stringResource(R.string.custom_time_before)) }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                if (selectedType == ReminderType.START_RELATIVE || selectedType == ReminderType.END_RELATIVE) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(stringResource(R.string.offset_before_event), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = offsetDays,
                            onValueChange = { offsetDays = it },
                            label = { Text(stringResource(R.string.days_before)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = offsetHours,
                            onValueChange = { offsetHours = it },
                            label = { Text(stringResource(R.string.hours)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = offsetMinutes,
                            onValueChange = { offsetMinutes = it },
                            label = { Text(stringResource(R.string.mins)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                } else {
                    // CUSTOM_ABSOLUTE
                    OutlinedTextField(
                        value = customTimeStr,
                        onValueChange = { customTimeStr = it },
                        label = { Text(stringResource(R.string.target_time_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = offsetDays,
                        onValueChange = { offsetDays = it },
                        label = { Text(stringResource(R.string.days_before)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                val targetDateForPreview = calculateNextDate(selectedCategory, eventTitle)
                val pd = offsetDays.toIntOrNull() ?: 0
                val ph = offsetHours.toIntOrNull() ?: 0
                val pm = offsetMinutes.toIntOrNull() ?: 15

                val pTriggerDT = when (selectedType) {
                    ReminderType.START_RELATIVE, ReminderType.END_RELATIVE -> {
                        targetDateForPreview.atTime(defaultStartTime).minusDays(pd.toLong()).minusHours(ph.toLong()).minusMinutes(pm.toLong())
                    }
                    ReminderType.CUSTOM_ABSOLUTE -> {
                        val ct = try { LocalTime.parse(customTimeStr) } catch (_: Exception) { LocalTime.of(6, 0) }
                        targetDateForPreview.minusDays(pd.toLong()).atTime(ct)
                    }
                }
                val previewStr = "${stringResource(R.string.alert_fires)}: ${pTriggerDT.format(DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a"))}"

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = previewStr,
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (eventTitle.isBlank()) return@TextButton
                val date = existingReminder?.eventDate ?: calculateNextDate(selectedCategory, eventTitle)
                val d = offsetDays.toIntOrNull() ?: 0
                val h = offsetHours.toIntOrNull() ?: 0
                val m = offsetMinutes.toIntOrNull() ?: 15

                val triggerDateTime = when (selectedType) {
                    ReminderType.START_RELATIVE, ReminderType.END_RELATIVE -> {
                        date.atTime(defaultStartTime).minusDays(d.toLong()).minusHours(h.toLong()).minusMinutes(m.toLong())
                    }
                    ReminderType.CUSTOM_ABSOLUTE -> {
                        val ct = try { LocalTime.parse(customTimeStr) } catch (_: Exception) { LocalTime.of(6, 0) }
                        date.minusDays(d.toLong()).atTime(ct)
                    }
                }
                val triggerInstant = triggerDateTime.atZone(ZoneId.systemDefault()).toInstant()

                val reminderId = existingReminder?.id ?: UUID.randomUUID().toString()
                val reminder = Reminder(
                    id = reminderId,
                    eventTitle = eventTitle,
                    eventCategory = selectedCategory,
                    eventDate = date,
                    triggerTime = triggerInstant,
                    reminderType = selectedType,
                    offsetDays = d,
                    offsetHours = h,
                    offsetMinutes = m,
                    isEnabled = true,
                    isRecurring = isRecurring,
                    timeDisplay = when (selectedType) {
                        ReminderType.CUSTOM_ABSOLUTE -> customTimeStr
                        else -> defaultStartTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                    }
                )

                val repo = RemindersRepository(context)
                repo.addReminder(reminder)
                ReminderWorker.scheduleReminder(context, reminder)

                onSaved()
                onDismiss()
            }) {
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
