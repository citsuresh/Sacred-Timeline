package com.suresh.sacredtimeline.data

import android.content.Context
import com.suresh.sacredtimeline.model.Reminder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class RemindersContainer(
    val version: Int = 1,
    val reminders: List<Reminder> = emptyList()
)

class RemindersRepository(private val context: Context) {
    private val fileName = "reminders_v1.json"
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    private fun getFile(): File = File(context.filesDir, fileName)

    fun getAllReminders(): List<Reminder> {
        return try {
            val file = getFile()
            if (!file.exists()) return emptyList()
            val content = file.readText()
            val container = json.decodeFromString<RemindersContainer>(content)
            container.reminders
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun saveReminders(reminders: List<Reminder>) {
        try {
            val container = RemindersContainer(reminders = reminders)
            val content = json.encodeToString(container)
            val file = getFile()
            val tempFile = File(context.filesDir, "$fileName.tmp")
            tempFile.writeText(content)
            if (tempFile.exists()) {
                tempFile.renameTo(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addReminder(reminder: Reminder) {
        val current = getAllReminders().toMutableList()
        current.removeIf { it.id == reminder.id }
        current.add(reminder)
        saveReminders(current)
    }

    fun removeReminder(id: String) {
        val current = getAllReminders().toMutableList()
        current.removeIf { it.id == id }
        saveReminders(current)
    }

    fun toggleReminder(id: String, isEnabled: Boolean) {
        val current = getAllReminders().map {
            if (it.id == id) it.copy(isEnabled = isEnabled) else it
        }
        saveReminders(current)
    }
}
