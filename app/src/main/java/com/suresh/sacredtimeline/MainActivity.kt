package com.suresh.sacredtimeline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.suresh.sacredtimeline.ui.dashboard.TimelineDashboard
import com.suresh.sacredtimeline.ui.navigation.NavRoute
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import com.suresh.sacredtimeline.ui.settings.SettingsScreen
import com.suresh.sacredtimeline.ui.theme.SacredTimelineTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.suresh.sacredtimeline.data.SettingsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val repository = SettingsRepository(this)

        setContent {
            // Use a specific state to hold the initial view mode and only set it once
            var startMode by remember { mutableStateOf<ViewMode?>(null) }
            
            LaunchedEffect(Unit) {
                if (startMode == null) {
                    startMode = repository.defaultLaunchView.first()
                }
            }
            
            if (startMode != null) {
                MainShell(initialMode = startMode!!)
            } else {
                // Show a splash or empty screen while loading settings
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

    @Composable
    fun MainShell(initialMode: ViewMode) {
        val backStack = remember { mutableStateListOf<Any>(NavRoute.Dashboard(initialMode)) }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val currentRoute = backStack.lastOrNull()

        SacredTimelineTheme {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Text(
                            "Sacred Timeline",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text("Composite View") },
                            selected = currentRoute is NavRoute.Dashboard && currentRoute.mode == ViewMode.COMPOSITE,
                            onClick = {
                                scope.launch {
                                    backStack.clear()
                                    backStack.add(NavRoute.Dashboard(ViewMode.COMPOSITE))
                                    drawerState.close()
                                }
                            },
                            icon = { Icon(Icons.Default.ViewColumn, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text("Nalla Neram") },
                            selected = currentRoute is NavRoute.Dashboard && currentRoute.mode == ViewMode.NERAM,
                            onClick = {
                                scope.launch {
                                    backStack.clear()
                                    backStack.add(NavRoute.Dashboard(ViewMode.NERAM))
                                    drawerState.close()
                                }
                            },
                            icon = { Icon(Icons.Default.Star, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text("Gowri Neram") },
                            selected = currentRoute is NavRoute.Dashboard && currentRoute.mode == ViewMode.GOWRI,
                            onClick = {
                                scope.launch {
                                    backStack.clear()
                                    backStack.add(NavRoute.Dashboard(ViewMode.GOWRI))
                                    drawerState.close()
                                }
                            },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text("Hora") },
                            selected = currentRoute is NavRoute.Dashboard && currentRoute.mode == ViewMode.HORA,
                            onClick = {
                                scope.launch {
                                    backStack.clear()
                                    backStack.add(NavRoute.Dashboard(ViewMode.HORA))
                                    drawerState.close()
                                }
                            },
                            icon = { Icon(Icons.Default.Schedule, contentDescription = null) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        NavigationDrawerItem(
                            label = { Text("Settings") },
                            selected = currentRoute is NavRoute.Settings,
                            onClick = {
                                if (currentRoute !is NavRoute.Settings) {
                                    backStack.add(NavRoute.Settings)
                                }
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                        )
                    }
                }
            ) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { 
                        if (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        } else {
                            this@MainActivity.finish()
                        }
                    },
                    entryProvider = { key ->
                        when (key) {
                            is NavRoute.Dashboard -> NavEntry(key) { 
                                TimelineDashboard(
                                    viewMode = key.mode,
                                    onMenuClick = { scope.launch { drawerState.open() } }
                                ) 
                            }
                            NavRoute.Settings -> NavEntry(key) { 
                                SettingsScreen(onBack = { 
                                    if (backStack.size > 1) {
                                        backStack.removeLastOrNull()
                                    }
                                })
                            }
                            else -> NavEntry(Unit) { Text("Unknown route") }
                        }
                    }
                )
            }
        }
    }
}
