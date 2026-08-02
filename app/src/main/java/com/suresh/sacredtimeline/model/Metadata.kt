package com.suresh.sacredtimeline.model

import android.content.Context
import androidx.annotation.StringRes
import com.suresh.sacredtimeline.R

object Metadata {
    @StringRes
    fun getPlanetNameRes(planet: String): Int {
        return when (planet) {
            "Sun" -> R.string.planet_sun
            "Moon" -> R.string.planet_moon
            "Mars" -> R.string.planet_mars
            "Mercury" -> R.string.planet_mercury
            "Jupiter" -> R.string.planet_jupiter
            "Venus" -> R.string.planet_venus
            "Saturn" -> R.string.planet_saturn
            else -> R.string.app_name // Fallback
        }
    }

    @StringRes
    fun getGowriNameRes(category: String): Int {
        return when (category) {
            "AMRIDHA" -> R.string.gowri_amridha
            "UTHI" -> R.string.gowri_uthi
            "LABAM" -> R.string.gowri_labam
            "DHANAM" -> R.string.gowri_dhanam
            "SUGAM" -> R.string.gowri_sugam
            "SORAM" -> R.string.gowri_soram
            "VISHAM" -> R.string.gowri_visham
            "ROGAM" -> R.string.gowri_rogam
            else -> R.string.app_name
        }
    }

    @StringRes
    fun getSpecialNameRes(name: String): Int {
        return when (name) {
            "Nalla" -> R.string.timing_nalla
            "Rahu" -> R.string.timing_rahu
            "Yama" -> R.string.timing_yama
            "Kuli", "Kuli Dawn", "Kuli Dusk" -> R.string.timing_kuli
            "Morning" -> R.string.timing_morning
            "Evening" -> R.string.timing_evening
            else -> R.string.app_name
        }
    }

    @StringRes
    fun getGowriDescriptionRes(category: String): Int {
        return when (category) {
            "AMRIDHA" -> R.string.desc_gowri_amridha
            "UTHI" -> R.string.desc_gowri_uthi
            "LABAM" -> R.string.desc_gowri_labam
            "DHANAM" -> R.string.desc_gowri_dhanam
            "SUGAM" -> R.string.desc_gowri_sugam
            "SORAM" -> R.string.desc_gowri_soram
            "VISHAM" -> R.string.desc_gowri_visham
            "ROGAM" -> R.string.desc_gowri_rogam
            else -> R.string.app_name
        }
    }

    @StringRes
    fun getSpecialDescriptionRes(name: String): Int {
        return when (name) {
            "Rahu" -> R.string.desc_timing_rahu
            "Yama" -> R.string.desc_timing_yama
            "Kuli", "Kuli Dawn", "Kuli Dusk" -> R.string.desc_timing_kuli
            "Morning", "Evening" -> R.string.desc_timing_nalla
            else -> R.string.app_name
        }
    }
    
    @StringRes
    fun getPlanetQualityRes(planet: String): Int {
        return when (planet) {
            "Sun" -> R.string.quality_sun
            "Moon" -> R.string.quality_moon
            "Mars" -> R.string.quality_mars
            "Mercury" -> R.string.quality_mercury
            "Jupiter" -> R.string.quality_jupiter
            "Venus" -> R.string.quality_venus
            "Saturn" -> R.string.quality_saturn
            else -> R.string.app_name
        }
    }

    @StringRes
    fun getHoraGuidanceRes(planet: String, compatibility: HoraCompatibility): Int {
        return when (planet) {
            "Sun" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.string.guidance_sun_fav
                HoraCompatibility.CONFLICTING -> R.string.guidance_sun_conf
                HoraCompatibility.NEUTRAL -> R.string.guidance_sun_neut
            }
            "Moon" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.string.guidance_moon_fav
                HoraCompatibility.CONFLICTING -> R.string.guidance_moon_conf
                HoraCompatibility.NEUTRAL -> R.string.guidance_moon_neut
            }
            "Mars" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.string.guidance_mars_fav
                HoraCompatibility.CONFLICTING -> R.string.guidance_mars_conf
                HoraCompatibility.NEUTRAL -> R.string.guidance_mars_neut
            }
            "Mercury" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.string.guidance_mercury_fav
                HoraCompatibility.CONFLICTING -> R.string.guidance_mercury_conf
                HoraCompatibility.NEUTRAL -> R.string.guidance_mercury_neut
            }
            "Jupiter" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.string.guidance_jupiter_fav
                HoraCompatibility.CONFLICTING -> R.string.guidance_jupiter_conf
                HoraCompatibility.NEUTRAL -> R.string.guidance_jupiter_neut
            }
            "Venus" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.string.guidance_venus_fav
                HoraCompatibility.CONFLICTING -> R.string.guidance_venus_conf
                HoraCompatibility.NEUTRAL -> R.string.guidance_venus_neut
            }
            "Saturn" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.string.guidance_saturn_fav
                HoraCompatibility.CONFLICTING -> R.string.guidance_saturn_conf
                HoraCompatibility.NEUTRAL -> R.string.guidance_saturn_neut
            }
            else -> R.string.guidance_default
        }
    }

    fun getPlanetName(planet: String, context: Context): String = context.getString(getPlanetNameRes(planet))
    fun getGowriName(category: String, context: Context): String = context.getString(getGowriNameRes(category))
    fun getSpecialName(name: String, context: Context): String = context.getString(getSpecialNameRes(name))
    fun getGowriDescription(category: String, context: Context): String = context.getString(getGowriDescriptionRes(category))
    fun getSpecialDescription(name: String, context: Context): String = context.getString(getSpecialDescriptionRes(name))
    fun getPlanetQuality(planet: String, context: Context): String = context.getString(getPlanetQualityRes(planet))
    fun getHoraGuidance(planet: String, compatibility: HoraCompatibility, context: Context): String = context.getString(getHoraGuidanceRes(planet, compatibility))

    fun getHoraStrategicActivities(planet: String, compatibility: HoraCompatibility, context: Context): List<String> {
        val resourceId = when (planet) {
            "Sun" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.array.activities_sun_fav
                HoraCompatibility.CONFLICTING -> R.array.activities_sun_conf
                HoraCompatibility.NEUTRAL -> R.array.activities_sun_neut
            }
            "Moon" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.array.activities_moon_fav
                HoraCompatibility.CONFLICTING -> R.array.activities_moon_conf
                HoraCompatibility.NEUTRAL -> R.array.activities_moon_neut
            }
            "Mars" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.array.activities_mars_fav
                HoraCompatibility.CONFLICTING -> R.array.activities_mars_conf
                HoraCompatibility.NEUTRAL -> R.array.activities_mars_neut
            }
            "Mercury" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.array.activities_mercury_fav
                HoraCompatibility.CONFLICTING -> R.array.activities_mercury_conf
                HoraCompatibility.NEUTRAL -> R.array.activities_mercury_neut
            }
            "Jupiter" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.array.activities_jupiter_fav
                HoraCompatibility.CONFLICTING -> R.array.activities_jupiter_conf
                HoraCompatibility.NEUTRAL -> R.array.activities_jupiter_neut
            }
            "Venus" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.array.activities_venus_fav
                HoraCompatibility.CONFLICTING -> R.array.activities_venus_conf
                HoraCompatibility.NEUTRAL -> R.array.activities_venus_neut
            }
            "Saturn" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> R.array.activities_saturn_fav
                HoraCompatibility.CONFLICTING -> R.array.activities_saturn_conf
                HoraCompatibility.NEUTRAL -> R.array.activities_saturn_neut
            }
            else -> return listOf(context.getString(R.string.guidance_default))
        }
        
        // Handle case where we might have returned a string resource ID by accident
        if (resourceId == R.string.guidance_default) {
             return listOf(context.getString(R.string.guidance_default))
        }

        return try {
            context.resources.getStringArray(resourceId).toList()
        } catch (e: Exception) {
            listOf(context.getString(R.string.guidance_default))
        }
    }
}
