package com.suresh.sacredtimeline.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
enum class ViewMode {
    COMPOSITE,
    NERAM,
    GOWRI,
    HORA
}

sealed interface NavRoute : NavKey {
    @Serializable
    data class Dashboard(val mode: ViewMode = ViewMode.COMPOSITE) : NavRoute

    @Serializable
    data object Settings : NavRoute
}
