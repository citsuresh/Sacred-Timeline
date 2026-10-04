package com.suresh.sacredtimeline.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate

@Serializable
enum class ReminderType {
    START_RELATIVE,
    END_RELATIVE,
    CUSTOM_ABSOLUTE
}

@Serializable
data class Reminder(
    val id: String,
    val eventTitle: String,
    val eventCategory: String, // NAKSHATRA, CHANDRASHTAMAM, GOWRI, MUHURTHAM, UNIVERSAL, etc.
    @Serializable(with = LocalDateSerializer::class)
    val eventDate: LocalDate,
    @Serializable(with = InstantSerializer::class)
    val triggerTime: Instant,
    val reminderType: ReminderType,
    val offsetDays: Int = 0,
    val offsetHours: Int = 0,
    val offsetMinutes: Int = 0,
    val isEnabled: Boolean = true,
    val isRecurring: Boolean = false,
    val timeDisplay: String = ""
)
