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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
                title = { Text(stringResource(R.string.reminders_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_reminder))
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
                    Text(stringResource(R.string.add_reminder))
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
                                val channel = NotificationChannel(channelId, context.getString(R.string.test_notifications_channel_name), NotificationManager.IMPORTANCE_HIGH).apply {
                                    description = context.getString(R.string.test_notifications_channel_desc)
                                    enableVibration(true)
                                }
                                notificationManager?.createNotificationChannel(channel)
                            }
                            val notification = NotificationCompat.Builder(context, channelId)
                                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                                .setContentTitle(context.getString(R.string.test_notification_title))
                                .setContentText(context.getString(R.string.test_notification_text))
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
                    Text(stringResource(R.string.test_notification))
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
                        Text(stringResource(R.string.no_reminders), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                        text = "${getLocalizedCategoryName(category, context)} (${categoryReminders.size})",
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

fun getLocalizedCategoryName(cat: String, context: Context): String {
    val resId = when (cat.uppercase()) {
        "NAKSHATRA" -> R.string.cat_nakshatra
        "MUHURTHAM" -> R.string.cat_muhurtham
        "NERAM" -> R.string.cat_neram
        "HORA" -> R.string.cat_hora
        "GOWRI" -> R.string.cat_gowri
        "TITHI" -> R.string.cat_tithi
        "CHANDRASHTAMAM" -> R.string.cat_chandrashtamam
        "TAMIL_FESTIVAL" -> R.string.cat_tamil_festival
        "SOLAR" -> R.string.cat_solar
        "YOGAM" -> R.string.cat_yogam
        "THARA_BALAM" -> R.string.cat_tharabalam
        "CUSTOM" -> R.string.cat_custom
        else -> R.string.cat_universal
    }
    return context.getString(resId)
}

@Composable
fun AddGenericReminderDialog(
    existingReminder: Reminder? = null,
    onDismiss: () -> Unit,
    onReminderAdded: () -> Unit
) {
    com.suresh.sacredtimeline.ui.dashboard.components.ReminderDialog(
        defaultTitle = existingReminder?.eventTitle ?: "Ashwini Nakshatra",
        defaultCategory = existingReminder?.eventCategory ?: "NAKSHATRA",
        existingReminder = existingReminder,
        onDismiss = onDismiss,
        onSaved = onReminderAdded
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
                    if (reminder.isRecurring) append(" • 🔁 ${stringResource(R.string.all_occurrences)}")
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
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_reminder), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(4.dp))
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
