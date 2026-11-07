package com.suresh.sacredtimeline.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import java.time.LocalDate

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveTimelineDashboard(
    modifier: Modifier = Modifier,
    viewModel: TimelineViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    viewMode: ViewMode = ViewMode.COMPOSITE,
    initialTargetDate: LocalDate? = null,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigateToScoringWeights: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    var selectedItem by remember { mutableStateOf<Any?>(null) }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            TimelineDashboard(
                modifier = modifier,
                viewModel = viewModel,
                viewMode = viewMode,
                initialTargetDate = initialTargetDate,
                onMenuClick = onMenuClick,
                onProfileClick = onProfileClick,
                onNavigateToScoringWeights = onNavigateToScoringWeights,
                onSearchClick = onSearchClick
            )
        },
        detailPane = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (selectedItem != null) {
                    Text(
                        text = stringResource(R.string.adaptive_detail_prefix, selectedItem.toString()),
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Text(
                        text = stringResource(R.string.adaptive_select_prompt),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}
