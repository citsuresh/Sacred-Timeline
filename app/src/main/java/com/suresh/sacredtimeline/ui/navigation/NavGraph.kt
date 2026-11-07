package com.suresh.sacredtimeline.ui.navigation

import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.data.BirthProfilesRepository
import com.suresh.sacredtimeline.ui.dashboard.AdaptiveTimelineDashboard
import com.suresh.sacredtimeline.ui.profile.ProfileScreen
import com.suresh.sacredtimeline.ui.search.SearchScreen
import com.suresh.sacredtimeline.ui.settings.*
import java.time.LocalDate

@Composable
fun MainNavGraph(
    backStack: MutableList<Any>,
    intent: Intent?,
    onOpenDrawer: () -> Unit,
    onFinish: () -> Unit
) {
    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            } else {
                onFinish()
            }
        },
        entryProvider = { key ->
            when (key) {
                is NavRoute.Dashboard -> NavEntry(key) {
                    val targetDateStr = intent?.getStringExtra("target_date")
                    val targetDate = remember {
                        targetDateStr?.let {
                            try { LocalDate.parse(it) } catch (_: Exception) { null }
                        }
                    }
                    AdaptiveTimelineDashboard(
                        viewMode = key.mode,
                        initialTargetDate = targetDate,
                        onMenuClick = onOpenDrawer,
                        onProfileClick = { backStack.add(NavRoute.Profile) },
                        onNavigateToScoringWeights = { backStack.add(NavRoute.ScoringWeightsSettings) },
                        onSearchClick = { backStack.add(NavRoute.Search) }
                    )
                }
                NavRoute.Search -> NavEntry(key) {
                    SearchScreen(
                        onBack = { backStack.removeLastOrNull() },
                        onDateSelected = { date ->
                            intent?.putExtra("target_date", date.toString())
                            backStack.clear()
                            backStack.add(NavRoute.Dashboard(ViewMode.UNIVERSAL))
                        }
                    )
                }
                NavRoute.Profile -> NavEntry(key) {
                    ProfileScreen(
                        repository = BirthProfilesRepository(androidx.compose.ui.platform.LocalContext.current),
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                NavRoute.Settings -> NavEntry(key) {
                    SettingsScreen(
                        onBack = {
                            backStack.removeLastOrNull()
                        },
                        onNavigateToTimelineDisplaySettings = {
                            backStack.add(NavRoute.CalendarSettings)
                        },
                        onNavigateToRemindersSettings = {
                            backStack.add(NavRoute.RemindersSettings)
                        },
                        onNavigateToScoringWeightsSettings = {
                            backStack.add(NavRoute.ScoringWeightsSettings)
                        }
                    )
                }
                NavRoute.ScoringWeightsSettings -> NavEntry(key) {
                    ScoringWeightsSettingsScreen(
                        onBack = {
                            backStack.removeLastOrNull()
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
                        },
                        onNavigateToChandrashtamamSettings = {
                            backStack.add(NavRoute.ChandrashtamamSettings)
                        }
                    )
                }
                NavRoute.ChandrashtamamSettings -> NavEntry(key) {
                    ChandrashtamamSettingsScreen(
                        onBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }
                NavRoute.RemindersSettings -> NavEntry(key) {
                    RemindersScreen(
                        onBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }
                else -> NavEntry(Unit) { Text(stringResource(R.string.unknown_route)) }
            }
        }
    )
}
