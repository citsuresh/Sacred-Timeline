package com.suresh.sacredtimeline.ui.theme

import androidx.compose.ui.graphics.Color
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
        }
    }

    fun getContentColor(backgroundColor: Color): Color {
        val luminance = 0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue
        return if (luminance > 0.6) Color.Black else Color.White
    }
}
