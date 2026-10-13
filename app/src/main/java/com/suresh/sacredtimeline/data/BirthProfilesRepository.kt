package com.suresh.sacredtimeline.data

import android.content.Context
import com.suresh.sacredtimeline.model.BirthProfile
import com.suresh.sacredtimeline.model.ChartStyle
import com.suresh.sacredtimeline.model.LunarMonthSystem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class BirthProfilesContainer(
    val version: Int = 1,
    val profiles: List<BirthProfile> = emptyList()
)

class BirthProfilesRepository(private val context: Context) {
    private val fileName = "birth_profiles_v1.json"
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    private fun getFile(): File = File(context.filesDir, fileName)

    fun getAllProfiles(): List<BirthProfile> {
        val profiles = try {
            val file = getFile()
            if (!file.exists()) {
                // Default profile for first start
                val defaultProfile = BirthProfile(
                    nameEnglish = "Default Profile",
                    nameRegional = "இயல்பு சுயவிவரம்",
                    birthDate = "01-01-1990",
                    birthTime = "06:00",
                    birthPlace = "Chennai, Tamil Nadu",
                    latitude = 13.0827,
                    longitude = 80.2707,
                    timezoneOffsetHours = 5.5,
                    chartStyle = ChartStyle.SOUTH_INDIAN,
                    lunarSystem = LunarMonthSystem.AMANTA,
                    isPrimary = true
                )
                val initial = listOf(defaultProfile)
                saveProfiles(initial)
                initial
            } else {
                val content = file.readText()
                val container = json.decodeFromString<BirthProfilesContainer>(content)
                container.profiles.ifEmpty {
                    listOf(
                        BirthProfile(
                            nameEnglish = "Default Profile",
                            nameRegional = "இயல்பு சுயவிவரம்",
                            birthDate = "01-01-1990",
                            birthTime = "06:00",
                            birthPlace = "Chennai, Tamil Nadu",
                            latitude = 13.0827,
                            longitude = 80.2707,
                            timezoneOffsetHours = 5.5,
                            chartStyle = ChartStyle.SOUTH_INDIAN,
                            lunarSystem = LunarMonthSystem.AMANTA,
                            isPrimary = true
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
        return profiles
    }

    fun saveProfiles(profiles: List<BirthProfile>) {
        try {
            val container = BirthProfilesContainer(profiles = profiles)
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

    fun addOrUpdateProfile(profile: BirthProfile) {
        val current = getAllProfiles().toMutableList()
        current.removeIf { it.id == profile.id }
        if (profile.isPrimary) {
            // Unset primary from others
            for (i in current.indices) {
                if (current[i].isPrimary) {
                    current[i] = current[i].copy(isPrimary = false)
                }
            }
        }
        if (current.isEmpty()) {
            current.add(profile.copy(isPrimary = true))
        } else {
            current.add(profile)
        }
        saveProfiles(current)
    }

    fun removeProfile(id: String) {
        val current = getAllProfiles().toMutableList()
        current.removeIf { it.id == id }
        if (current.none { it.isPrimary } && current.isNotEmpty()) {
            current[0] = current[0].copy(isPrimary = true)
        }
        saveProfiles(current)
    }

    fun setPrimary(id: String) {
        val current = getAllProfiles().map {
            it.copy(isPrimary = it.id == id)
        }
        saveProfiles(current)
    }

    fun getPrimaryProfile(): BirthProfile? {
        val profiles = getAllProfiles()
        return profiles.find { it.isPrimary } ?: profiles.firstOrNull()
    }
}
