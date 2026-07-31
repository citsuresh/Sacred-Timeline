package com.suresh.sacredtimeline.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface NavRoute : NavKey {
    @Serializable
    data object Dashboard : NavRoute
}
