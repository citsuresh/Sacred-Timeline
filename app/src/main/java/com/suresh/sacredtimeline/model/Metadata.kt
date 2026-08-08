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
            "Maitra Muhurtham" -> R.string.timing_maitra
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
            "Abhijit Muhurtham" -> R.string.muhurtham_desc_abhijit
            "Brahma Muhurtham" -> R.string.muhurtham_desc_brahma
            "Maitra Muhurtham" -> R.string.timing_desc_maitra
            else -> R.string.app_name
        }
    }

    @StringRes
    fun getCategoryShortNameRes(timing: Timing): Int {
        return when (timing) {
            is NallaNeram, is SpecialPeriod -> R.string.label_neram_short
            is GowriNeram -> R.string.nav_gowri_neram
            is Hora -> R.string.nav_hora
            is Muhurtham, is MaitraMuhurtham -> R.string.label_muhurtham_short
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

    @StringRes
    fun getTithiDescriptionRes(value: Int): Int {
        val t = value % 30
        val normalized = if (t == 0) 30 else t
        return when (normalized) {
            1 -> R.string.desc_tithi_1
            2 -> R.string.desc_tithi_2
            3 -> R.string.desc_tithi_3
            4 -> R.string.desc_tithi_4
            5 -> R.string.desc_tithi_5
            6 -> R.string.desc_tithi_6
            7 -> R.string.desc_tithi_7
            8 -> R.string.desc_tithi_8
            9 -> R.string.desc_tithi_9
            10 -> R.string.desc_tithi_10
            11 -> R.string.desc_tithi_11
            12 -> R.string.desc_tithi_12
            13 -> R.string.desc_tithi_13
            14 -> R.string.desc_tithi_14
            15 -> R.string.desc_tithi_15
            30 -> R.string.desc_tithi_30
            else -> R.string.desc_tithi_1
        }
    }

    @StringRes
    fun getNakshatraDescriptionRes(value: Int): Int {
        return when (value) {
            1 -> R.string.desc_star_1
            2 -> R.string.desc_star_2
            3 -> R.string.desc_star_3
            4 -> R.string.desc_star_4
            5 -> R.string.desc_star_5
            6 -> R.string.desc_star_6
            7 -> R.string.desc_star_7
            8 -> R.string.desc_star_8
            9 -> R.string.desc_star_9
            10 -> R.string.desc_star_10
            11 -> R.string.desc_star_11
            12 -> R.string.desc_star_12
            13 -> R.string.desc_star_13
            14 -> R.string.desc_star_14
            15 -> R.string.desc_star_15
            16 -> R.string.desc_star_16
            17 -> R.string.desc_star_17
            18 -> R.string.desc_star_18
            19 -> R.string.desc_star_19
            20 -> R.string.desc_star_20
            21 -> R.string.desc_star_21
            22 -> R.string.desc_star_22
            23 -> R.string.desc_star_23
            24 -> R.string.desc_star_24
            25 -> R.string.desc_star_25
            26 -> R.string.desc_star_26
            27 -> R.string.desc_star_27
            else -> R.string.star_desc_generic
        }
    }

    @StringRes
    fun getEventDescriptionRes(resId: Int): Int {
        return when (resId) {
            R.string.event_pradosham -> R.string.event_desc_pradosham
            R.string.event_sivaratri -> R.string.event_desc_sivaratri
            R.string.event_aadi_kiruthigai -> R.string.event_desc_aadi_kiruthigai
            R.string.event_aadi_pooram -> R.string.event_desc_aadi_pooram
            R.string.month_chithirai, R.string.month_vaikasi, R.string.month_aani,
            R.string.month_aadi, R.string.month_avani, R.string.month_purattasi,
            R.string.month_aippasi, R.string.month_karthigai, R.string.month_margazhi,
            R.string.month_thai, R.string.month_maasi, R.string.month_panguni -> R.string.desc_month_generic
            R.string.year_1, R.string.year_2, R.string.year_3, R.string.year_4, R.string.year_5,
            R.string.year_6, R.string.year_7, R.string.year_8, R.string.year_9, R.string.year_10,
            R.string.year_11, R.string.year_12, R.string.year_13, R.string.year_14, R.string.year_15,
            R.string.year_16, R.string.year_17, R.string.year_18, R.string.year_19, R.string.year_20,
            R.string.year_21, R.string.year_22, R.string.year_23, R.string.year_24, R.string.year_25,
            R.string.year_26, R.string.year_27, R.string.year_28, R.string.year_29, R.string.year_30,
            R.string.year_31, R.string.year_32, R.string.year_33, R.string.year_34, R.string.year_35,
            R.string.year_36, R.string.year_37, R.string.year_38, R.string.year_39, R.string.year_40,
            R.string.year_41, R.string.year_42, R.string.year_43, R.string.year_44, R.string.year_45,
            R.string.year_46, R.string.year_47, R.string.year_48, R.string.year_49, R.string.year_50,
            R.string.year_51, R.string.year_52, R.string.year_53, R.string.year_54, R.string.year_55,
            R.string.year_56, R.string.year_57, R.string.year_58, R.string.year_59, R.string.year_60 -> R.string.event_desc_tamil_date
            else -> R.string.event_desc_generic
        }
    }

    @StringRes
    fun getMuhurthamDescriptionRes(resId: Int): Int {
        return when (resId) {
            R.string.muhurtham_brahma -> R.string.muhurtham_desc_brahma
            R.string.muhurtham_abhijit -> R.string.muhurtham_desc_abhijit
            R.string.muhurtham_subha -> R.string.muhurtham_desc_subha
            else -> R.string.app_name
        }
    }

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

    @StringRes
    fun getMuhurthamNameRes(name: String): Int {
        return when (name) {
            "Brahma Muhurtham" -> R.string.muhurtham_brahma
            "Abhijit Muhurtham" -> R.string.muhurtham_abhijit
            else -> R.string.app_name
        }
    }

    @StringRes
    fun getMaitraPotencyRes(stars: Int): Int {
        return if (stars == 5) R.string.maitra_potency_high else R.string.maitra_potency_standard
    }
}
