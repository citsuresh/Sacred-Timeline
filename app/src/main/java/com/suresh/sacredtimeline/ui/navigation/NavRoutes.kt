package com.suresh.sacredtimeline.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
enum class ViewMode {
    COMPOSITE,
    UNIVERSAL,
    NERAM_MUHURTHAM,
    NERAM,
    BRAHMA,
    ABHIJIT,
    GOWRI,
    HORA,
    MAITRA
}

sealed interface NavRoute : NavKey {
    @Serializable
    data class Dashboard(val mode: ViewMode = ViewMode.COMPOSITE) : NavRoute

    @Serializable
    data object Settings : NavRoute

    @Serializable
    data object CalendarSettings : NavRoute

    @Serializable
    data object TithiSettings : NavRoute

    @Serializable
    data object NakshatraSettings : NavRoute
}
