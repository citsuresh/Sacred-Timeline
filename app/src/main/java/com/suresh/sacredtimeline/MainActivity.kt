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

            val navOwner = rememberNavigationEventDispatcherOwner(parent = null)
            CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides navOwner) {
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
        val repository = remember { SettingsRepository(this) }
        val backStack = remember { mutableStateListOf<Any>(NavRoute.Dashboard(initialMode)) }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        
        val hasCustomLayout by repository.hasCustomLayout.collectAsState(initial = false)
        var showSwitchConfirm by remember { mutableStateOf<ViewMode?>(null) }

        val isDarkTheme = when (themeMode) {
            "LIGHT" -> false
            "DARK" -> true
            else -> androidx.compose.foundation.isSystemInDarkTheme()
        }

        if (showSwitchConfirm != null) {
            AlertDialog(
                onDismissRequest = { showSwitchConfirm = null },
                title = { Text(stringResource(R.string.confirm_switch_title)) },
                text = { Text(stringResource(R.string.confirm_switch_text)) },
                confirmButton = {
                    TextButton(onClick = {
                        val mode = showSwitchConfirm!!
                        showSwitchConfirm = null
                        scope.launch {
                            val targetColId = when (mode) {
                                ViewMode.UNIVERSAL -> null
                                ViewMode.NERAM_MUHURTHAM -> "NERAM_MUHURTHAM"
                                ViewMode.NERAM -> "NERAM"
                                ViewMode.GOWRI -> "GOWRI"
                                ViewMode.HORA -> "HORA"
                                ViewMode.MAITRA -> "MAITRA"
                                else -> null
                            }
                            repository.setSingleVisibleColumn(targetColId)
                            repository.setDefaultLaunchView(mode)
                            backStack.clear()
                            backStack.add(NavRoute.Dashboard(mode))
                            drawerState.close()
                        }
                    }) {
                        Text(stringResource(R.string.btn_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSwitchConfirm = null }) {
                        Text(stringResource(R.string.btn_cancel))
                    }
                }
            )
        }

        SacredTimelineTheme(darkTheme = isDarkTheme) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        val currentRoute = backStack.lastOrNull()
                        val currentViewMode = (currentRoute as? NavRoute.Dashboard)?.mode
                        
                        Text(
                            stringResource(R.string.app_name),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        HorizontalDivider()
                        
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_universal)) },
                            selected = currentViewMode == ViewMode.UNIVERSAL,
                            onClick = {
                                if (currentViewMode == ViewMode.CUSTOM) {
                                    showSwitchConfirm = ViewMode.UNIVERSAL
                                } else {
                                    scope.launch {
                                        repository.setSingleVisibleColumn(null)
                                        backStack.clear()
                                        backStack.add(NavRoute.Dashboard(ViewMode.UNIVERSAL))
                                        drawerState.close()
                                    }
                                }
                            },
                            icon = { Icon(Icons.Default.AllInclusive, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_neram_muhurtham)) },
                            selected = currentViewMode == ViewMode.NERAM_MUHURTHAM,
                            onClick = {
                                if (currentViewMode == ViewMode.CUSTOM) {
                                    showSwitchConfirm = ViewMode.NERAM_MUHURTHAM
                                } else {
                                    scope.launch {
                                        repository.setSingleVisibleColumn("NERAM_MUHURTHAM")
                                        backStack.clear()
                                        backStack.add(NavRoute.Dashboard(ViewMode.NERAM_MUHURTHAM))
                                        drawerState.close()
                                    }
                                }
                            },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_nalla_neram)) },
                            selected = currentViewMode == ViewMode.NERAM,
                            onClick = {
                                if (currentViewMode == ViewMode.CUSTOM) {
                                    showSwitchConfirm = ViewMode.NERAM
                                } else {
                                    scope.launch {
                                        repository.setSingleVisibleColumn("NERAM")
                                        backStack.clear()
                                        backStack.add(NavRoute.Dashboard(ViewMode.NERAM))
                                        drawerState.close()
                                    }
                                }
                            },
                            icon = { Icon(Icons.Default.Star, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_gowri_neram)) },
                            selected = currentViewMode == ViewMode.GOWRI,
                            onClick = {
                                if (currentViewMode == ViewMode.CUSTOM) {
                                    showSwitchConfirm = ViewMode.GOWRI
                                } else {
                                    scope.launch {
                                        repository.setSingleVisibleColumn("GOWRI")
                                        backStack.clear()
                                        backStack.add(NavRoute.Dashboard(ViewMode.GOWRI))
                                        drawerState.close()
                                    }
                                }
                            },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_hora)) },
                            selected = currentViewMode == ViewMode.HORA,
                            onClick = {
                                if (currentViewMode == ViewMode.CUSTOM) {
                                    showSwitchConfirm = ViewMode.HORA
                                } else {
                                    scope.launch {
                                        repository.setSingleVisibleColumn("HORA")
                                        backStack.clear()
                                        backStack.add(NavRoute.Dashboard(ViewMode.HORA))
                                        drawerState.close()
                                    }
                                }
                            },
                            icon = { Icon(Icons.Default.Schedule, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_maitra)) },
                            selected = currentViewMode == ViewMode.MAITRA,
                            onClick = {
                                if (currentViewMode == ViewMode.CUSTOM) {
                                    showSwitchConfirm = ViewMode.MAITRA
                                } else {
                                    scope.launch {
                                        repository.setSingleVisibleColumn("MAITRA")
                                        backStack.clear()
                                        backStack.add(NavRoute.Dashboard(ViewMode.MAITRA))
                                        drawerState.close()
                                    }
                                }
                            },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        if (hasCustomLayout) {
                            NavigationDrawerItem(
                                label = { Text(stringResource(R.string.nav_custom)) },
                                selected = currentViewMode == ViewMode.CUSTOM,
                                onClick = {
                                    scope.launch {
                                        repository.restoreCustomLayout()
                                        repository.setDefaultLaunchView(ViewMode.CUSTOM)
                                        backStack.clear()
                                        backStack.add(NavRoute.Dashboard(ViewMode.CUSTOM))
                                        drawerState.close()
                                    }
                                },
                                icon = { Icon(Icons.Default.DashboardCustomize, contentDescription = null) }
                            )
                        }

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
                                        backStack.removeLastOrNull()
                                    },
                                    onNavigateToTimelineDisplaySettings = {
                                        backStack.add(NavRoute.CalendarSettings)
                                    }
                                )
                            }
                            NavRoute.TithiSettings -> NavEntry(key) {
                                TithiSettingsScreen(
                                    onBack = {
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }
                            NavRoute.NakshatraSettings -> NavEntry(key) {
                                NakshatraSettingsScreen(
                                    onBack = {
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }
                            NavRoute.CalendarSettings -> NavEntry(key) {
                                TimelineDisplaySettingsScreen(
                                    onBack = {
                                        backStack.removeLastOrNull()
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
