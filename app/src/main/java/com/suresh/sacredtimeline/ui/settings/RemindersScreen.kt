package com.suresh.sacredtimeline.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { RemindersRepository(context) }
    var reminders by remember { mutableStateOf(repository.getAllReminders()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<Reminder?>(null) }

    val grouped = reminders.groupBy { it.eventCategory.uppercase() }
    var expandedCategories by remember(reminders) {
        mutableStateOf(grouped.keys.associateWith { true })
    }

    if (showAddDialog || editingReminder != null) {
        AddGenericReminderDialog(
            existingReminder = editingReminder,
            onDismiss = {
                showAddDialog = false
                editingReminder = null
            },
            onReminderAdded = {
                reminders = repository.getAllReminders()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scheduled Reminders & Alerts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Reminder")
                }

                OutlinedButton(
                    onClick = {
                        val manager = NotificationManagerCompat.from(context)
                        if (!manager.areNotificationsEnabled()) {
                            android.widget.Toast.makeText(context, "Notifications are disabled in system settings! Please enable them.", android.widget.Toast.LENGTH_LONG).show()
                            try {
                                val intent = android.content.Intent().apply {
                                    action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                                    putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            val channelId = "sacred_timeline_alerts_v2"
                            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val channel = NotificationChannel(channelId, "Test Notifications", NotificationManager.IMPORTANCE_HIGH).apply {
                                    description = "Test notifications channel"
                                    enableVibration(true)
                                }
                                notificationManager?.createNotificationChannel(channel)
                            }
                            val notification = NotificationCompat.Builder(context, channelId)
                                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                                .setContentTitle("Sacred Timeline Test")
                                .setContentText("Test notification triggered manually!")
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setAutoCancel(true)
                                .build()
                            try {
                                manager.notify(8888, notification)
                            } catch (e: SecurityException) {
                                e.printStackTrace()
                                android.widget.Toast.makeText(context, "Failed to send notification: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Notification")
                }
            }

            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No reminders scheduled", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    grouped.forEach { (category, categoryReminders) ->
                        val isExpanded = expandedCategories[category] ?: true
                        item(key = "header_$category") {
                            Surface(
                                onClick = {
                                    expandedCategories = expandedCategories.toMutableMap().apply {
                                        this[category] = !isExpanded
                                    }
                                },
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${category.replace("_", " ")} (${categoryReminders.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        if (isExpanded) {
                            items(categoryReminders, key = { it.id }) { reminder ->
                                ReminderCard(
                                    reminder = reminder,
                                    onEdit = {
                                        editingReminder = reminder
                                    },
                                    onToggle = { enabled ->
                                        repository.toggleReminder(reminder.id, enabled)
                                        reminders = repository.getAllReminders()
                                        if (enabled) {
                                            ReminderWorker.scheduleReminder(context, reminder.copy(isEnabled = true))
                                        } else {
                                            ReminderWorker.cancelReminder(context, reminder.id)
                                        }
                                    },
                                    onDelete = {
                                        repository.removeReminder(reminder.id)
                                        ReminderWorker.cancelReminder(context, reminder.id)
                                        reminders = repository.getAllReminders()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddGenericReminderDialog(
    existingReminder: Reminder? = null,
    onDismiss: () -> Unit,
    onReminderAdded: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(existingReminder?.eventCategory ?: "NAKSHATRA") }
    var eventTitle by remember { mutableStateOf(existingReminder?.eventTitle ?: "Ashwini Nakshatra") }
    
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

    var timeStr by remember { mutableStateOf("06:00") }
    var offsetDays by remember { mutableStateOf(existingReminder?.offsetDays?.toString() ?: "0") }
    var offsetHours by remember { mutableStateOf(existingReminder?.offsetHours?.toString() ?: "0") }
    var offsetMinutes by remember { mutableStateOf(existingReminder?.offsetMinutes?.toString() ?: "15") }
    var selectedType by remember { mutableStateOf(existingReminder?.reminderType ?: ReminderType.START_RELATIVE) }
    var isRecurring by remember { mutableStateOf(existingReminder?.isRecurring ?: false) }

    var expandedCategory by remember { mutableStateOf(false) }
    val categories = listOf("NAKSHATRA", "TITHI", "CHANDRASHTAMAM", "YOGAM", "THARA_BALAM", "MUHURTHAM", "NERAM", "HORA", "GOWRI", "TAMIL_FESTIVAL", "SOLAR", "CUSTOM", "UNIVERSAL")

    var expandedItem by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingReminder == null) "Add Reminder" else "Edit Reminder") },
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
                        Text("Category: $selectedCategory")
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

                if (selectedCategory.uppercase() == "CUSTOM" || selectedCategory.uppercase() == "UNIVERSAL") {
                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text("Event Name / Title") },
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

                Text("Frequency:", style = MaterialTheme.typography.bodyMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isRecurring,
                        onClick = { isRecurring = false },
                        label = { Text("Single Occurrence") }
                    )
                    FilterChip(
                        selected = isRecurring,
                        onClick = { isRecurring = true },
                        label = { Text("All Occurrences") }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Reminder Strategy:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = selectedType == ReminderType.START_RELATIVE,
                            onClick = { selectedType = ReminderType.START_RELATIVE },
                            label = { Text("Relative to Start") }
                        )
                        FilterChip(
                            selected = selectedType == ReminderType.END_RELATIVE,
                            onClick = { selectedType = ReminderType.END_RELATIVE },
                            label = { Text("Relative to End") }
                        )
                    }
                    FilterChip(
                        selected = selectedType == ReminderType.CUSTOM_ABSOLUTE,
                        onClick = { selectedType = ReminderType.CUSTOM_ABSOLUTE },
                        label = { Text("Custom Time (N Days Before)") }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                if (selectedType == ReminderType.START_RELATIVE || selectedType == ReminderType.END_RELATIVE) {
                    val defaultTimeForCat = when (selectedCategory.uppercase()) {
                        "MUHURTHAM" -> if (eventTitle.contains("Brahma")) LocalTime.of(4, 30) else LocalTime.of(6, 0)
                        "SOLAR" -> if (eventTitle.contains("Sunrise")) LocalTime.of(6, 10) else LocalTime.of(18, 10)
                        else -> LocalTime.of(6, 0)
                    }
                    if (timeStr == "06:00") timeStr = defaultTimeForCat.format(DateTimeFormatter.ofPattern("HH:mm"))

                    OutlinedTextField(
                        value = timeStr,
                        onValueChange = { timeStr = it },
                        label = { Text("Event Start Time (HH:MM)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Offset Before Event:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = offsetDays,
                            onValueChange = { offsetDays = it },
                            label = { Text("Days") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = offsetHours,
                            onValueChange = { offsetHours = it },
                            label = { Text("Hours") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = offsetMinutes,
                            onValueChange = { offsetMinutes = it },
                            label = { Text("Mins") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = timeStr,
                        onValueChange = { timeStr = it },
                        label = { Text("Target Notification Time (HH:MM)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = offsetDays,
                        onValueChange = { offsetDays = it },
                        label = { Text("Days Before Event Date (N)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                val targetDateForPreview = calculateNextDate(selectedCategory, eventTitle)
                val previewTime = try { LocalTime.parse(timeStr) } catch (_: Exception) { LocalTime.of(6, 0) }
                val pd = offsetDays.toIntOrNull() ?: 0
                val ph = offsetHours.toIntOrNull() ?: 0
                val pm = offsetMinutes.toIntOrNull() ?: 15

                val pTriggerDT = when (selectedType) {
                    ReminderType.START_RELATIVE, ReminderType.END_RELATIVE -> {
                        targetDateForPreview.atTime(previewTime).minusDays(pd.toLong()).minusHours(ph.toLong()).minusMinutes(pm.toLong())
                    }
                    ReminderType.CUSTOM_ABSOLUTE -> {
                        targetDateForPreview.minusDays(pd.toLong()).atTime(previewTime)
                    }
                }
                val previewStr = "🔔 Alert fires: ${pTriggerDT.format(DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a"))}"

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
                val time = try { LocalTime.parse(timeStr) } catch (_: Exception) { LocalTime.of(6, 0) }
                val d = offsetDays.toIntOrNull() ?: 0
                val h = offsetHours.toIntOrNull() ?: 0
                val m = offsetMinutes.toIntOrNull() ?: 15

                val triggerDateTime = when (selectedType) {
                    ReminderType.START_RELATIVE, ReminderType.END_RELATIVE -> {
                        date.atTime(time).minusDays(d.toLong()).minusHours(h.toLong()).minusMinutes(m.toLong())
                    }
                    ReminderType.CUSTOM_ABSOLUTE -> {
                        date.minusDays(d.toLong()).atTime(time)
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
                    timeDisplay = time.format(DateTimeFormatter.ofPattern("hh:mm a"))
                )

                val repo = RemindersRepository(context)
                repo.addReminder(reminder)
                ReminderWorker.scheduleReminder(context, reminder)

                onReminderAdded()
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    onEdit: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.eventTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                val offsetText = buildString {
                    append(reminder.reminderType.name.replace("_", " ").lowercase())
                    if (reminder.offsetDays > 0) append(" • ${reminder.offsetDays}d")
                    if (reminder.offsetHours > 0) append(" • ${reminder.offsetHours}h")
                    if (reminder.offsetMinutes > 0) append(" • ${reminder.offsetMinutes}m before")
                    if (reminder.isRecurring) append(" • 🔁 All Occurrences")
                }
                Text(
                    text = offsetText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Event Date: ${reminder.eventDate.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Reminder", tint = MaterialTheme.colorScheme.primary)
                }
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = onToggle
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Reminder", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
