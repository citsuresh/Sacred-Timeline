package com.suresh.sacredtimeline.data

import android.content.Context
import com.suresh.sacredtimeline.model.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate

@Serializable
data class CacheContainer(
    val version: Int,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val data: Map<@Serializable(with = LocalDateSerializer::class) LocalDate, DayData>
)

class CacheManager(private val context: Context) {
    private val fileName = "panchangam_cache.json"
    private val currentVersion = 1
    
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    fun saveCache(latitude: Double, longitude: Double, data: Map<LocalDate, DayData>) {
        try {
            val container = CacheContainer(
                version = currentVersion,
                timestamp = System.currentTimeMillis(),
                latitude = latitude,
                longitude = longitude,
                data = data
            )
            val jsonString = json.encodeToString(container)
            val file = File(context.cacheDir, fileName)
            val tempFile = File(context.cacheDir, "$fileName.tmp")
            
            // Atomic save: write to tmp then rename
            tempFile.writeText(jsonString)
            if (tempFile.exists()) {
                tempFile.renameTo(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadCache(currentLat: Double, currentLng: Double): Map<LocalDate, DayData>? {
        return try {
            val file = File(context.cacheDir, fileName)
            if (!file.exists()) return null
            
            val jsonString = file.readText()
            val container = json.decodeFromString<CacheContainer>(jsonString)
            
            // 1. Check version
            if (container.version != currentVersion) return null
            
            // 2. Check location (max 1km difference approx)
            val dist = Math.abs(container.latitude - currentLat) + Math.abs(container.longitude - currentLng)
            if (dist > 0.01) return null // Roughly 1km
            
            // 3. Check timestamp (optional, we use sliding window anyway)
            // if (System.currentTimeMillis() - container.timestamp > 7 * 24 * 3600 * 1000) return null

            container.data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearCache() {
        try {
            val file = File(context.cacheDir, fileName)
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
