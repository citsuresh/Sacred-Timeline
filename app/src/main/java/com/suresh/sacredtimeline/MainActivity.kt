package com.suresh.sacredtimeline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.suresh.sacredtimeline.ui.dashboard.TimelineDashboard
import com.suresh.sacredtimeline.ui.navigation.NavRoute
import com.suresh.sacredtimeline.ui.theme.SacredTimelineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val backStack = remember { mutableStateListOf<Any>(NavRoute.Dashboard) }

            SacredTimelineTheme {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = { key ->
                        when (key) {
                            NavRoute.Dashboard -> NavEntry(key) { TimelineDashboard() }
                            else -> NavEntry(Unit) { Text("Unknown route") }
                        }
                    }
                )
            }
        }
    }
}
