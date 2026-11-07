package com.suresh.sacredtimeline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.ui.navigation.MainDrawerContent
import com.suresh.sacredtimeline.ui.navigation.MainNavGraph
import com.suresh.sacredtimeline.ui.navigation.NavRoute
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import com.suresh.sacredtimeline.ui.theme.SacredTimelineTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val backStack = mutableStateListOf<Any>()

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        android.util.Log.d("MainActivity", "onBackPressed called, backStack size = ${backStack.size}")
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else {
            super.onBackPressed()
        }
    }

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
                        Box(contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(
                                    text = stringResource(R.string.loading_preferences),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MainShell(initialMode: ViewMode, themeMode: String) {
        val repository = remember { SettingsRepository(this) }
        if (backStack.isEmpty()) {
            backStack.add(NavRoute.Dashboard(initialMode))
        }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        
        val hasCustomLayout by repository.hasCustomLayout.collectAsState(initial = false)
        var showSwitchConfirm by remember { mutableStateOf<ViewMode?>(null) }

        val isDarkTheme = when (themeMode) {
            "LIGHT" -> false
            "DARK" -> true
            else -> androidx.compose.foundation.isSystemInDarkTheme()
        }

        SacredTimelineTheme(darkTheme = isDarkTheme) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    MainDrawerContent(
                        backStack = backStack,
                        drawerState = drawerState,
                        scope = scope,
                        repository = repository,
                        hasCustomLayout = hasCustomLayout,
                        showSwitchConfirm = showSwitchConfirm,
                        onShowSwitchConfirmChange = { showSwitchConfirm = it }
                    )
                }
            ) {
                MainNavGraph(
                    backStack = backStack,
                    intent = intent,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onFinish = { finish() }
                )
            }
        }
    }
}
