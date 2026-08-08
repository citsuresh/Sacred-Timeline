package com.suresh.sacredtimeline.ui.theme

import androidx.compose.ui.graphics.Color
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.*

object SacredTimelineColors {
    fun getTimingColor(timing: Timing): Color {
        return when (timing) {
            is NallaNeram -> {
                if (timing.name == "Morning") NallaMorningGreen else NallaEveningGreen
            }
            is GowriNeram -> {
                when (timing.name) {
                    "SUGAM", "LABAM", "DHANAM" -> GowriStandardGreen
                    "AMRIDHA" -> GowriAmridhaBest
                    "SORAM", "VISHAM", "ROGAM" -> GowriStandardRed
                    "UTHI" -> GowriUthiBlue
                    else -> GowriStandardGreen
                }
            }
            is Hora -> {
                when (timing.name) {
                    "Jupiter" -> HoraJupiter
                    "Venus" -> HoraVenus
                    "Mercury" -> HoraMercury
                    "Moon" -> HoraMoon
                    "Mars" -> HoraMars
                    "Saturn" -> HoraSaturn
                    "Sun" -> HoraSun
                    else -> HoraSun
                }
            }
            is SpecialPeriod -> {
                when {
                    timing.name == "Rahu" -> RahuBrightRed
                    timing.name == "Yama" -> YamaMaroon
                    timing.name == "Kuli Dawn" -> KuliDawnGrey
                    timing.name == "Kuli Dusk" -> KuliDuskGrey
                    else -> KuliDawnGrey
                }
            }
            is Muhurtham -> {
                if (timing.name == "Abhijit Muhurtham") Color(0xFF4CAF50) else Color(0xFFE91E63)
            }
            is MaitraMuhurtham -> {
                if (timing.potencyStars == 5) MaitraGold else MaitraGoldPale
            }
        }
    }

    fun getSubhaMuhurthamTextColor(): Color = SubhaMuhurthamGold
    fun getSubhaMuhurthamBackgroundColor(): Color = SubhaMuhurthamPale

    fun getEventColors(resId: Int): Pair<Color, Color> {
        val isTraditional = when (resId) {
            R.string.event_pongal, R.string.event_thiruvalluvar_day, 
            R.string.event_tamil_new_year, R.string.event_deepavali,
            R.string.event_aadi_perukku, R.string.event_aadi_pooram,
            R.string.event_aadi_kiruthigai,
            R.string.event_pradosham, R.string.event_sivaratri,
            R.string.event_naga_chaturthi, R.string.event_naga_panchami,
            R.string.event_garuda_panchami, R.string.event_vinayagar_chaturthi,
            R.string.event_sankatahara_chaturthi -> true
            else -> false
        }
        
        return if (isTraditional) {
            SubhaMuhurthamGold to SubhaMuhurthamPale
        } else {
            HolidayPurple to HolidayPale
        }
    }

    fun getContentColor(backgroundColor: Color): Color {
        val luminance = 0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue
        return if (luminance > 0.6) Color.Black else Color.White
    }
}
