package com.suresh.sacredtimeline.ui.settings

import android.app.Application
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import kotlinx.coroutines.Dispatchers
import com.suresh.sacredtimeline.worker.WidgetUpdateWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

    init {
        // Automatically reschedule widget work when interval changes
        viewModelScope.launch {
            repository.widgetRefreshMinutes.collect { minutes ->
                WidgetUpdateWorker.enqueuePeriodicWork(getApplication(), minutes.toLong())
            }
        }
    }

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

    val locationMode: StateFlow<String> = repository.locationMode.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "AUTO"
    )

    val manualCityName: StateFlow<String> = repository.manualCityName.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "Coimbatore"
    )

    val widgetRefreshMinutes: StateFlow<Int> = repository.widgetRefreshMinutes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 30
    )

    val preloadDays: StateFlow<Int> = repository.preloadDays.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState

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

    fun setLocationMode(mode: String) {
        viewModelScope.launch { repository.setLocationMode(mode) }
    }

    fun setManualCityName(name: String) {
        viewModelScope.launch { repository.setManualCityName(name) }
    }

    fun searchCity(name: String) {
        if (name.isBlank()) return
        
        viewModelScope.launch {
            _searchState.value = SearchState.Searching
            try {
                val addresses = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(getApplication(), Locale.getDefault())
                    geocoder.getFromLocationName(name, 5)
                }
                
                if (addresses.isNullOrEmpty()) {
                    _searchState.value = SearchState.Error("City not found")
                } else {
                    _searchState.value = SearchState.Results(addresses)
                }
            } catch (e: Exception) {
                _searchState.value = SearchState.Error("Search failed: ${e.message}")
            }
        }
    }

    fun selectCity(address: Address) {
        val cityName = address.locality ?: address.subAdminArea ?: address.featureName ?: "Unknown"
        val displayName = buildString {
            append(cityName)
            address.adminArea?.let { append(", $it") }
            address.countryName?.let { append(", $it") }
        }
        
        viewModelScope.launch {
            repository.setManualCityName(displayName)
            repository.updateManualCoordinates(address.latitude, address.longitude)
            _searchState.value = SearchState.Idle
        }
    }

    fun clearSearch() {
        _searchState.value = SearchState.Idle
    }

    fun setWidgetRefreshMinutes(minutes: Int) {
        viewModelScope.launch { repository.setWidgetRefreshMinutes(minutes) }
    }

    fun setPreloadDays(days: Int) {
        viewModelScope.launch { repository.setPreloadDays(days) }
    }
}

sealed interface SearchState {
    data object Idle : SearchState
    data object Searching : SearchState
    data class Results(val addresses: List<Address>) : SearchState
    data class Error(val message: String) : SearchState
}
