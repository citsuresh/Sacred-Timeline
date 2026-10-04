package com.suresh.sacredtimeline.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.logic.LunarCalendarUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChandrashtamamSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val enabledStars by viewModel.enabledChandrashtamamStars.collectAsState()

    // 36 items: 27 base, but 9 are split
    val options = remember {
        val list = mutableListOf<Pair<String, Int>>()
        for (i in 1..27) {
            val starResId = when (i) {
                1 -> R.string.star_1
                2 -> R.string.star_2
                3 -> R.string.star_3
                4 -> R.string.star_4
                5 -> R.string.star_5
                6 -> R.string.star_6
                7 -> R.string.star_7
                8 -> R.string.star_8
                9 -> R.string.star_9
                10 -> R.string.star_10
                11 -> R.string.star_11
                12 -> R.string.star_12
                13 -> R.string.star_13
                14 -> R.string.star_14
                15 -> R.string.star_15
                16 -> R.string.star_16
                17 -> R.string.star_17
                18 -> R.string.star_18
                19 -> R.string.star_19
                20 -> R.string.star_20
                21 -> R.string.star_21
                22 -> R.string.star_22
                23 -> R.string.star_23
                24 -> R.string.star_24
                25 -> R.string.star_25
                26 -> R.string.star_26
                27 -> R.string.star_27
                else -> R.string.star_1
            }

            if (isSplitStar(i)) {
                list.add("STAR_${i}_1" to starResId)
                list.add("STAR_${i}_2" to starResId)
            } else {
                list.add("STAR_$i" to starResId)
            }
        }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_chandrashtamam_options)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val allSelected = options.all { enabledStars.contains(it.first) }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select All / Deselect All", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = allSelected,
                        onCheckedChange = { viewModel.setAllEnabledChandrashtamamStars(it) }
                    )
                }
                HorizontalDivider()
            }

            items(options) { (id, starResId) ->
                val rasiIdx = LunarCalendarUtils.getBirthRasi(id)
                val rasiResId = when (rasiIdx) {
                    1 -> R.string.rasi_1
                    2 -> R.string.rasi_2
                    3 -> R.string.rasi_3
                    4 -> R.string.rasi_4
                    5 -> R.string.rasi_5
                    6 -> R.string.rasi_6
                    7 -> R.string.rasi_7
                    8 -> R.string.rasi_8
                    9 -> R.string.rasi_9
                    10 -> R.string.rasi_10
                    11 -> R.string.rasi_11
                    12 -> R.string.rasi_12
                    else -> R.string.rasi_1
                }
                
                SettingsToggleItem(
                    label = stringResource(starResId) + " (" + stringResource(rasiResId) + ")",
                    checked = enabledStars.contains(id),
                    onCheckedChange = { viewModel.updateEnabledChandrashtamamStar(id, it) }
                )
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

private fun isSplitStar(index: Int): Boolean {
    return index == 3 || index == 5 || index == 7 || index == 12 || index == 14 || index == 16 || index == 21 || index == 23 || index == 25
}
