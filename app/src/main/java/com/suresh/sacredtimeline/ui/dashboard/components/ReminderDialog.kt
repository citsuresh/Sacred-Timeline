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
    defaultCategory: String = "CUSTOM",
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
    var selectedCategory by remember { 
        val rawCat = existingReminder?.eventCategory ?: defaultCategory
        mutableStateOf(if (rawCat.uppercase() == "UNIVERSAL") "CUSTOM" else rawCat)
    }
    var isTamilDateSelection by remember { mutableStateOf(existingReminder?.isTamilDate ?: false) }
    var customEnglishDateStr by remember { mutableStateOf(existingReminder?.eventDate?.toString() ?: LocalDate.now().toString()) }
    var selectedTamilMonth by remember { mutableStateOf(existingReminder?.tamilMonth.takeIf { !it.isNullOrBlank() } ?: "Thai") }
    var selectedTamilStarOrTithi by remember { mutableStateOf(existingReminder?.tamilStarOrTithi.takeIf { !it.isNullOrBlank() } ?: "Ashwini Nakshatra") }
    
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
        "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Pournami",
        "Pratipada (Krishna)", "Dwitiya (Krishna)", "Tritiya (Krishna)", "Chaturthi (Krishna)", "Panchami (Krishna)",
        "Shashti (Krishna)", "Saptami (Krishna)", "Ashtami (Krishna)", "Navami (Krishna)", "Dashami (Krishna)",
        "Ekadashi (Krishna)", "Dwadashi (Krishna)", "Trayodashi (Krishna)", "Chaturdashi (Krishna)", "Amavasai"
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
            "CUSTOM" -> {
                if (isTamilDateSelection) {
                    val monthResId = when (selectedTamilMonth.lowercase()) {
                        "chithirai" -> R.string.month_chithirai
                        "vaikasi" -> R.string.month_vaikasi
                        "aani" -> R.string.month_aani
                        "aadi" -> R.string.month_aadi
                        "avani" -> R.string.month_avani
                        "purattasi" -> R.string.month_purattasi
                        "aippasi" -> R.string.month_aippasi
                        "karthigai" -> R.string.month_karthigai
                        "margazhi" -> R.string.month_margazhi
                        "thai" -> R.string.month_thai
                        "maasi" -> R.string.month_maasi
                        "panguni" -> R.string.month_panguni
                        else -> R.string.month_thai
                    }

                    val isStar = selectedTamilStarOrTithi.contains("Nakshatra", true)
                    val starIdx = if (isStar) nakshatraNames.indexOf(selectedTamilStarOrTithi) + 1 else -1
                    val tithiIdx = if (!isStar) tithiNames.indexOf(selectedTamilStarOrTithi) + 1 else -1

                    var foundDate: LocalDate? = null
                    for (y in today.year..(today.year + 1)) {
                        val start = LocalDate.of(y, 1, 1)
                        val end = LocalDate.of(y, 12, 31)
                        var curr = start
                        while (!curr.isAfter(end)) {
                            try {
                                val tamilDate = TamilCalendarUtils.getTamilDate(curr)
                                if (tamilDate.monthResId == monthResId) {
                                    val info = LunarCalendarUtils.getLunarDayInfo(curr, "AMANTA")
                                    val matches = if (isStar) {
                                        info.nakshatras.any { it.value == starIdx }
                                    } else {
                                        info.tithis.any { it.value == tithiIdx }
                                    }
                                    if (matches) {
                                        if (!curr.isBefore(today)) {
                                            return curr
                                        }
                                        if (foundDate == null) {
                                            foundDate = curr
                                        }
                                    }
                                }
                            } catch (_: Exception) {}
                            curr = curr.plusDays(1)
                        }
                    }
                    return foundDate ?: today
                } else {
                    try {
                        val parsed = LocalDate.parse(customEnglishDateStr)
                        var target = LocalDate.of(today.year, parsed.month, parsed.dayOfMonth)
                        if (target.isBefore(today) || target == today) {
                            target = target.plusYears(1)
                        }
                        return target
                    } catch (_: Exception) {
                        return today
                    }
                }
            }
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

    fun calculateEventStartTime(cat: String, title: String, date: LocalDate): LocalTime {
        when (cat.uppercase()) {
            "NAKSHATRA" -> {
                val starIdx = nakshatraNames.indexOf(title) + 1
                if (starIdx in 1..27) {
                    try {
                        val info = LunarCalendarUtils.getLunarDayInfo(date, "AMANTA")
                        info.nakshatras.find { it.value == starIdx }?.startTime?.let { instant ->
                            return instant.atZone(ZoneId.systemDefault()).toLocalTime()
                        }
                    } catch (_: Exception) {}
                }
            }
            "TITHI" -> {
                val tithiIdx = tithiNames.indexOf(title) + 1
                if (tithiIdx in 1..30) {
                    try {
                        val info = LunarCalendarUtils.getLunarDayInfo(date, "AMANTA")
                        info.tithis.find { it.value == tithiIdx }?.startTime?.let { instant ->
                            return instant.atZone(ZoneId.systemDefault()).toLocalTime()
                        }
                    } catch (_: Exception) {}
                }
            }
            "SOLAR" -> {
                try {
                    val sun = com.suresh.sacredtimeline.logic.SunriseSunsetProvider().getSunTimes(13.0827, 80.2707, date)
                    if (title.contains("Sunrise", true)) return sun.sunrise else if (title.contains("Sunset", true)) return sun.sunset
                } catch (_: Exception) {}
            }
            else -> {}
        }
        return defaultStartTime
    }

    var customTimeStr by remember { mutableStateOf("06:00") }
    var offsetDays by remember { mutableStateOf(existingReminder?.offsetDays?.toString() ?: "0") }
    var offsetHours by remember { mutableStateOf(existingReminder?.offsetHours?.toString() ?: "0") }
    var offsetMinutes by remember { mutableStateOf(existingReminder?.offsetMinutes?.toString() ?: "15") }
    var selectedType by remember { mutableStateOf(existingReminder?.reminderType ?: ReminderType.START_RELATIVE) }
    var isRecurring by remember { mutableStateOf(existingReminder?.isRecurring ?: false) }

    var expandedTamilMonth by remember { mutableStateOf(false) }
    var expandedTamilStar by remember { mutableStateOf(false) }
    val tamilMonths = listOf("Chithirai", "Vaikasi", "Aani", "Aadi", "Avani", "Purattasi", "Aippasi", "Karthigai", "Margazhi", "Thai", "Maasi", "Panguni")

    var expandedCategory by remember { mutableStateOf(false) }
    val categories = listOf("NAKSHATRA", "TITHI", "CHANDRASHTAMAM", "YOGAM", "THARA_BALAM", "MUHURTHAM", "NERAM", "HORA", "GOWRI", "TAMIL_FESTIVAL", "SOLAR", "CUSTOM")

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

                if (selectedCategory.uppercase() == "CUSTOM") {
                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text(stringResource(R.string.event_title_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Date Selection Mode", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !isTamilDateSelection,
                            onClick = { isTamilDateSelection = false },
                            label = { Text(stringResource(R.string.label_english_date)) }
                        )
                        FilterChip(
                            selected = isTamilDateSelection,
                            onClick = { isTamilDateSelection = true },
                            label = { Text(stringResource(R.string.label_tamil_date)) }
                        )
                    }

                    if (!isTamilDateSelection) {
                        OutlinedTextField(
                            value = customEnglishDateStr,
                            onValueChange = { customEnglishDateStr = it },
                            label = { Text("Event Date (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expandedTamilMonth = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Tamil Month: $selectedTamilMonth")
                            }
                            DropdownMenu(
                                expanded = expandedTamilMonth,
                                onDismissRequest = { expandedTamilMonth = false }
                            ) {
                                tamilMonths.forEach { month ->
                                    DropdownMenuItem(
                                        text = { Text(month) },
                                        onClick = {
                                            selectedTamilMonth = month
                                            expandedTamilMonth = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expandedTamilStar = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Star / Tithi: $selectedTamilStarOrTithi")
                            }
                            DropdownMenu(
                                expanded = expandedTamilStar,
                                onDismissRequest = { expandedTamilStar = false }
                            ) {
                                (nakshatraNames + tithiNames).forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            selectedTamilStarOrTithi = item
                                            expandedTamilStar = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expandedItem = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.event_title_format, eventTitle))
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
                val actualStartTime = remember(selectedCategory, eventTitle, targetDateForPreview) {
                    val calc = calculateEventStartTime(selectedCategory, eventTitle, targetDateForPreview)
                    if (calc != LocalTime.of(6, 0) || defaultStartTime == LocalTime.of(6, 0)) calc else defaultStartTime
                }
                val pd = offsetDays.toIntOrNull() ?: 0
                val ph = offsetHours.toIntOrNull() ?: 0
                val pm = offsetMinutes.toIntOrNull() ?: 15

                val pTriggerDT = when (selectedType) {
                    ReminderType.START_RELATIVE, ReminderType.END_RELATIVE -> {
                        targetDateForPreview.atTime(actualStartTime).minusDays(pd.toLong()).minusHours(ph.toLong()).minusMinutes(pm.toLong())
                    }
                    ReminderType.CUSTOM_ABSOLUTE -> {
                        val ct = try { LocalTime.parse(customTimeStr) } catch (_: Exception) { LocalTime.of(6, 0) }
                        targetDateForPreview.minusDays(pd.toLong()).atTime(ct)
                    }
                }
                val previewStr = if (selectedCategory.uppercase() == "CUSTOM" && isRecurring) {
                    val dateFormatted = targetDateForPreview.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                    "${stringResource(R.string.alert_fires)}: $dateFormatted • $customTimeStr (Repeats annually on same day every year)"
                } else {
                    "${stringResource(R.string.alert_fires)}: ${pTriggerDT.format(DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a"))}"
                }

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
                val date = calculateNextDate(selectedCategory, eventTitle)
                val resolvedStartTime = calculateEventStartTime(selectedCategory, eventTitle, date)
                val d = offsetDays.toIntOrNull() ?: 0
                val h = offsetHours.toIntOrNull() ?: 0
                val m = offsetMinutes.toIntOrNull() ?: 15

                val triggerDateTime = when (selectedType) {
                    ReminderType.START_RELATIVE, ReminderType.END_RELATIVE -> {
                        date.atTime(resolvedStartTime).minusDays(d.toLong()).minusHours(h.toLong()).minusMinutes(m.toLong())
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
                    isRecurring = isRecurring || selectedCategory.uppercase() == "CUSTOM",
                    timeDisplay = when (selectedType) {
                        ReminderType.CUSTOM_ABSOLUTE -> customTimeStr
                        else -> resolvedStartTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                    },
                    isTamilDate = isTamilDateSelection,
                    tamilMonth = selectedTamilMonth,
                    tamilStarOrTithi = selectedTamilStarOrTithi
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
