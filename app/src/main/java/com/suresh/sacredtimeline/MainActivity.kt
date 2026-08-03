package com.suresh.sacredtimeline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.ui.dashboard.TimelineDashboard
import com.suresh.sacredtimeline.ui.navigation.NavRoute
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import com.suresh.sacredtimeline.ui.settings.SettingsScreen
import com.suresh.sacredtimeline.ui.settings.TimelineDisplaySettingsScreen
import com.suresh.sacredtimeline.ui.settings.TithiSettingsScreen
import com.suresh.sacredtimeline.ui.settings.NakshatraSettingsScreen
import com.suresh.sacredtimeline.ui.theme.SacredTimelineTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val repository = SettingsRepository(this)

        setContent {
            // Get the current locales from the system/delegate immediately
            val currentLocale = remember { 
                val locales = AppCompatDelegate.getApplicationLocales()
                if (locales.isEmpty) "en" else locales.toLanguageTags().split(",")[0]
            }
            
            val language by repository.language.collectAsState(initial = currentLocale)
            val themeMode by repository.themeMode.collectAsState(initial = "SYSTEM")

            LaunchedEffect(language) {
                val currentTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                val currentBase = if (currentTags.isEmpty()) "en" else currentTags.split("-")[0]
                val requestedBase = language.split("-")[0]
                
                if (currentBase != requestedBase) {
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                }
            }

            // Provide the NavigationEventDispatcherOwner to avoid the crash in Nav3/Compose
            val navOwner = rememberNavigationEventDispatcherOwner(parent = null)
            CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides navOwner) {
                // Use a specific state to hold the initial view mode and only set it once
                var startMode by remember { mutableStateOf<ViewMode?>(null) }
                
                LaunchedEffect(Unit) {
                    if (startMode == null) {
                        startMode = repository.defaultLaunchView.first()
                    }
                }
                
                if (startMode != null) {
                    MainShell(initialMode = startMode!!, themeMode = themeMode)
                } else {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MainShell(initialMode: ViewMode, themeMode: String) {
        val backStack = remember { mutableStateListOf<Any>(NavRoute.Dashboard(initialMode)) }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val currentRoute = backStack.lastOrNull()

        val isDarkTheme = when (themeMode) {
            "LIGHT" -> false
            "DARK" -> true
            else -> androidx.compose.foundation.isSystemInDarkTheme()
        }

        SacredTimelineTheme(darkTheme = isDarkTheme) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Text(
                            stringResource(R.string.app_name),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_composite)) },
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
                            label = { Text(stringResource(R.string.nav_nalla_neram)) },
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
                            label = { Text(stringResource(R.string.nav_gowri_neram)) },
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
                            label = { Text(stringResource(R.string.nav_hora)) },
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
                            label = { Text(stringResource(R.string.nav_settings)) },
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
                                SettingsScreen(
                                    onBack = { 
                                        if (backStack.size > 1) {
                                            backStack.removeLastOrNull()
                                        }
                                    },
                                    onNavigateToTimelineDisplaySettings = {
                                        backStack.add(NavRoute.CalendarSettings)
                                    }
                                )
                            }
                            NavRoute.TithiSettings -> NavEntry(key) {
                                TithiSettingsScreen(
                                    onBack = {
                                        if (backStack.size > 1) {
                                            backStack.removeLastOrNull()
                                        }
                                    }
                                )
                            }
                            NavRoute.NakshatraSettings -> NavEntry(key) {
                                NakshatraSettingsScreen(
                                    onBack = {
                                        if (backStack.size > 1) {
                                            backStack.removeLastOrNull()
                                        }
                                    }
                                )
                            }
                            NavRoute.CalendarSettings -> NavEntry(key) {
                                TimelineDisplaySettingsScreen(
                                    onBack = {
                                        if (backStack.size > 1) {
                                            backStack.removeLastOrNull()
                                        }
                                    },
                                    onNavigateToTithiSettings = {
                                        backStack.add(NavRoute.TithiSettings)
                                    },
                                    onNavigateToNakshatraSettings = {
                                        backStack.add(NavRoute.NakshatraSettings)
                                    }
                                )
                            }
                            else -> NavEntry(Unit) { Text(stringResource(R.string.unknown_route)) }
                        }
                    }
                )
            }
        }
    }
}
