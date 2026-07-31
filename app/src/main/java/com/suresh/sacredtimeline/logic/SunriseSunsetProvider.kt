package com.suresh.sacredtimeline.logic

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

interface SunriseSunsetApi {
    @GET("json")
    suspend fun getSunriseSunset(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("date") date: String,
        @Query("formatted") formatted: Int = 0
    ): SunriseSunsetResponse
}

data class SunriseSunsetResponse(
    @Json(name = "results") val results: SunriseSunsetResults,
    @Json(name = "status") val status: String
)

data class SunriseSunsetResults(
    @Json(name = "sunrise") val sunrise: String,
    @Json(name = "sunset") val sunset: String
)

data class SunTimesResult(
    val sunrise: LocalTime,
    val sunset: LocalTime,
    val isFallback: Boolean
)

class SunriseSunsetProvider {

    private val api: SunriseSunsetApi

    init {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.sunrise-sunset.org/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        api = retrofit.create(SunriseSunsetApi::class.java)
    }

    suspend fun getSunTimes(lat: Double, lng: Double, date: LocalDate): SunTimesResult {
        return try {
            val response = api.getSunriseSunset(lat, lng, date.toString())
            if (response.status == "OK") {
                val sunriseUtc = ZonedDateTime.parse(response.results.sunrise)
                val sunsetUtc = ZonedDateTime.parse(response.results.sunset)
                
                // Convert UTC to local device time
                val sunriseLocal = sunriseUtc.withZoneSameInstant(ZonedDateTime.now().zone).toLocalTime()
                val sunsetLocal = sunsetUtc.withZoneSameInstant(ZonedDateTime.now().zone).toLocalTime()
                
                SunTimesResult(sunriseLocal, sunsetLocal, false)
            } else {
                fallback()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fallback()
        }
    }

    private fun fallback(): SunTimesResult {
        return SunTimesResult(LocalTime.of(6, 0), LocalTime.of(18, 0), true)
    }
}
