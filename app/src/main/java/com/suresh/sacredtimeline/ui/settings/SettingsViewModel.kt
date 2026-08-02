package com.suresh.sacredtimeline.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

    val compositeScale: StateFlow<Float> = repository.compositeScale.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f
    )
    
    val singleViewScale: StateFlow<Float> = repository.singleViewScale.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0.5f
    )

    val columnVisibility: StateFlow<Set<String>> = repository.columnVisibility.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("NERAM", "GOWRI", "HORA")
    )

    val columnOrder: StateFlow<List<String>> = repository.columnOrder.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("NERAM", "GOWRI", "HORA")
    )

    val defaultLaunchView: StateFlow<ViewMode> = repository.defaultLaunchView.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ViewMode.COMPOSITE
    )

    val timeFormat24h: StateFlow<Boolean> = repository.timeFormat24h.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val showNowLine: StateFlow<Boolean> = repository.showNowLine.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val pinchToZoomEnabled: StateFlow<Boolean> = repository.pinchToZoomEnabled.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    fun updateCompositeScale(scale: Float) {
        viewModelScope.launch { repository.updateCompositeScale(scale) }
    }

    fun updateSingleViewScale(scale: Float) {
        viewModelScope.launch { repository.updateSingleViewScale(scale) }
    }

    fun updateColumnVisibility(column: String, visible: Boolean) {
        viewModelScope.launch { repository.updateColumnVisibility(column, visible) }
    }

    fun moveColumn(column: String, direction: Int) { // -1 for up, 1 for down
        val currentOrder = columnOrder.value.toMutableList()
        val index = currentOrder.indexOf(column)
        if (index == -1) return
        val newIndex = index + direction
        if (newIndex in 0 until currentOrder.size) {
            currentOrder.removeAt(index)
            currentOrder.add(newIndex, column)
            viewModelScope.launch { repository.updateColumnOrder(currentOrder) }
        }
    }

    fun setDefaultLaunchView(mode: ViewMode) {
        viewModelScope.launch { repository.setDefaultLaunchView(mode) }
    }

    fun setTimeFormat24h(is24h: Boolean) {
        viewModelScope.launch { repository.setTimeFormat24h(is24h) }
    }

    fun setShowNowLine(show: Boolean) {
        viewModelScope.launch { repository.setShowNowLine(show) }
    }

    fun setPinchToZoomEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setPinchToZoomEnabled(enabled) }
    }
}
