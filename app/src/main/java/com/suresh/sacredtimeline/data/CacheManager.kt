package com.suresh.sacredtimeline.data

import android.content.Context
import com.suresh.sacredtimeline.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    private val fileName = "panchangam_cache_v13.json"
    private val currentVersion = 13
    
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    private fun getFile(): File = File(context.cacheDir, fileName)

    suspend fun saveCache(latitude: Double, longitude: Double, data: Map<LocalDate, DayData>) = withContext(Dispatchers.IO) {
        try {
            val container = CacheContainer(
                version = currentVersion,
                timestamp = System.currentTimeMillis(),
                latitude = latitude,
                longitude = longitude,
                data = data
            )
            val jsonString = json.encodeToString(container)
            val file = getFile()
            val tempFile = File(context.cacheDir, "$fileName.tmp")
            
            // Atomic save: write to tmp then delete existing file and rename tmp
            tempFile.writeText(jsonString)
            if (file.exists()) {
                file.delete()
            }
            if (tempFile.exists()) {
                tempFile.renameTo(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadCache(currentLat: Double, currentLng: Double): Map<LocalDate, DayData>? = withContext(Dispatchers.IO) {
        try {
            val file = getFile()
            if (!file.exists()) return@withContext null
            
            val jsonString = file.readText()
            val container = json.decodeFromString<CacheContainer>(jsonString)
            
            // 1. Check version
            if (container.version != currentVersion) return@withContext null
            
            // 2. Check location (max 1km difference approx)
            val dist = Math.abs(container.latitude - currentLat) + Math.abs(container.longitude - currentLng)
            if (dist > 0.01) return@withContext null // Roughly 1km

            container.data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun clearCache() = withContext(Dispatchers.IO) {
        try {
            val file = getFile()
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
