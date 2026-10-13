package com.suresh.sacredtimeline.ui.profile

import androidx.compose.ui.graphics.Color
import com.suresh.sacredtimeline.ui.theme.*

fun getPlanetColor(planet: String): Color {
    return when (planet) {
        "Sun" -> SunColor
        "Moon" -> MoonColor
        "Mars" -> MarsColor
        "Mercury" -> MercuryColor
        "Jupiter" -> JupiterColor
        "Venus" -> VenusColor
        "Saturn" -> SaturnColor
        "Rahu" -> RahuColor
        "Ketu" -> KetuColor
        else -> Color(0xFF757575)
    }
}

fun getPlanetFgColor(planet: String): Color {
    return when (planet) {
        "Sun" -> SunFgColor
        "Moon" -> MoonFgColor
        "Mars" -> MarsFgColor
        "Mercury" -> MercuryFgColor
        "Jupiter" -> JupiterFgColor
        "Venus" -> VenusFgColor
        "Saturn" -> SaturnFgColor
        "Rahu" -> RahuFgColor
        "Ketu" -> KetuFgColor
        else -> Color.White
    }
}
