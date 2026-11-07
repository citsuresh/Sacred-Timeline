package com.suresh.sacredtimeline.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainDrawerContent(
    backStack: MutableList<Any>,
    drawerState: DrawerState,
    scope: CoroutineScope,
    repository: SettingsRepository,
    hasCustomLayout: Boolean,
    showSwitchConfirm: ViewMode?,
    onShowSwitchConfirmChange: (ViewMode?) -> Unit
) {
    val currentRoute = backStack.lastOrNull()
    val currentViewMode = (currentRoute as? NavRoute.Dashboard)?.mode

    if (showSwitchConfirm != null) {
        AlertDialog(
            onDismissRequest = { onShowSwitchConfirmChange(null) },
            title = { Text(stringResource(R.string.confirm_switch_title)) },
            text = { Text(stringResource(R.string.confirm_switch_text)) },
            confirmButton = {
                TextButton(onClick = {
                    val mode = showSwitchConfirm
                    onShowSwitchConfirmChange(null)
                    scope.launch {
                        val targetColId = when (mode) {
                            ViewMode.UNIVERSAL -> null
                            ViewMode.NERAM_MUHURTHAM -> "NERAM_MUHURTHAM"
                            ViewMode.NERAM -> "NERAM"
                            ViewMode.GOWRI -> "GOWRI"
                            ViewMode.HORA -> "HORA"
                            ViewMode.MAITRA -> "MAITRA"
                            ViewMode.CHANDRASHTAMAM -> "CHANDRASHTAMAM"
                            ViewMode.YOGAM -> "YOGAM"
                            ViewMode.THARA_BALAM -> "THARA_BALAM"
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
                TextButton(onClick = { onShowSwitchConfirmChange(null) }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp)
        ) {
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
                        onShowSwitchConfirmChange(ViewMode.UNIVERSAL)
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
                        onShowSwitchConfirmChange(ViewMode.NERAM_MUHURTHAM)
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
                        onShowSwitchConfirmChange(ViewMode.NERAM)
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
                        onShowSwitchConfirmChange(ViewMode.GOWRI)
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
                        onShowSwitchConfirmChange(ViewMode.HORA)
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
                        onShowSwitchConfirmChange(ViewMode.MAITRA)
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
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.nav_chandrashtamam)) },
                selected = currentViewMode == ViewMode.CHANDRASHTAMAM,
                onClick = {
                    if (currentViewMode == ViewMode.CUSTOM) {
                        onShowSwitchConfirmChange(ViewMode.CHANDRASHTAMAM)
                    } else {
                        scope.launch {
                            repository.setSingleVisibleColumn("CHANDRASHTAMAM")
                            backStack.clear()
                            backStack.add(NavRoute.Dashboard(ViewMode.CHANDRASHTAMAM))
                            drawerState.close()
                        }
                    }
                },
                icon = { Icon(Icons.Default.Warning, contentDescription = null) }
            )
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.nav_yoga)) },
                selected = currentViewMode == ViewMode.YOGAM,
                onClick = {
                    if (currentViewMode == ViewMode.CUSTOM) {
                        onShowSwitchConfirmChange(ViewMode.YOGAM)
                    } else {
                        scope.launch {
                            repository.setSingleVisibleColumn("YOGAM")
                            backStack.clear()
                            backStack.add(NavRoute.Dashboard(ViewMode.YOGAM))
                            drawerState.close()
                        }
                    }
                },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) }
            )
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.nav_thara_balam)) },
                selected = currentViewMode == ViewMode.THARA_BALAM,
                onClick = {
                    if (currentViewMode == ViewMode.CUSTOM) {
                        onShowSwitchConfirmChange(ViewMode.THARA_BALAM)
                    } else {
                        scope.launch {
                            repository.setSingleVisibleColumn("THARA_BALAM")
                            backStack.clear()
                            backStack.add(NavRoute.Dashboard(ViewMode.THARA_BALAM))
                            drawerState.close()
                        }
                    }
                },
                icon = { Icon(Icons.Default.Stars, contentDescription = null) }
            )

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

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.nav_profile)) },
                selected = currentRoute is NavRoute.Profile,
                onClick = {
                    if (currentRoute !is NavRoute.Profile) {
                        backStack.add(NavRoute.Profile)
                    }
                    scope.launch { drawerState.close() }
                },
                icon = { Icon(Icons.Default.Person, contentDescription = null) }
            )

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.nav_search)) },
                selected = currentRoute is NavRoute.Search,
                onClick = {
                    if (currentRoute !is NavRoute.Search) {
                        backStack.add(NavRoute.Search)
                    }
                    scope.launch { drawerState.close() }
                },
                icon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
        }
    }
}
