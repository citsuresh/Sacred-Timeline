package com.suresh.sacredtimeline.ui.settings

import android.app.Application
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.data.CacheManager
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
    private val cacheManager = CacheManager(application)

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
        viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("UNIVERSAL")
    )

    val columnOrder: StateFlow<List<String>> = repository.columnOrder.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("NERAM_MUHURTHAM", "UNIVERSAL", "NERAM", "BRAHMA", "ABHIJIT", "GOWRI", "HORA")
    )

    val widgetColumnVisibility: StateFlow<Set<String>> = repository.widgetColumnVisibility.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("UNIVERSAL")
    )

    val widgetColumnOrder: StateFlow<List<String>> = repository.widgetColumnOrder.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("NERAM_MUHURTHAM", "UNIVERSAL", "NERAM", "BRAHMA", "ABHIJIT", "GOWRI", "HORA")
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

    val nowLineColor: StateFlow<Int> = repository.nowLineColor.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF4CAF50.toInt()
    )

    val nowLineThickness: StateFlow<Float> = repository.nowLineThickness.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 2.0f
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
        viewModelScope, SharingStarted.WhileSubscribed(5000), 30)

    val enabledTithis: StateFlow<Set<String>> = repository.enabledTithis.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet()
    )

    val enabledNakshatras: StateFlow<Set<String>> = repository.enabledNakshatras.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet()
    )

    val showTamilDate: StateFlow<Boolean> = repository.showTamilDate.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val showTamilYear: StateFlow<Boolean> = repository.showTamilYear.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val showPirai: StateFlow<Boolean> = repository.showPirai.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val showSunrise: StateFlow<Boolean> = repository.showSunrise.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val showSunset: StateFlow<Boolean> = repository.showSunset.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val showBrahmaMuhurtham: StateFlow<Boolean> = repository.showBrahmaMuhurtham.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val showAbhijitMuhurtham: StateFlow<Boolean> = repository.showAbhijitMuhurtham.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val showMaitraMuhurtham: StateFlow<Boolean> = repository.showMaitraMuhurtham.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val sunriseDefinition: StateFlow<String> = repository.sunriseDefinition.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "SCIENTIFIC"
    )

    val specialPeriodStyle: StateFlow<String> = repository.specialPeriodStyle.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "PROPORTIONAL"
    )

    val lunarMonthSystem: StateFlow<String> = repository.lunarMonthSystem.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "AMANTA"
    )

    val timelineViewStyle: StateFlow<String> = repository.timelineViewStyle.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "EQUAL_DISTRIBUTION"
    )

    val hasCustomLayout: StateFlow<Boolean> = repository.hasCustomLayout.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val language: StateFlow<String> = repository.language.stateIn(
        viewModelScope, 
        SharingStarted.WhileSubscribed(5000), 
        AppCompatDelegate.getApplicationLocales().let { locales ->
            if (locales.isEmpty()) "en" else locales.toLanguageTags().split(",")[0]
        }
    )

    val themeMode: StateFlow<String> = repository.themeMode.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM"
    )

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState

    fun updateCompositeScale(scale: Float) {
        viewModelScope.launch { repository.updateCompositeScale(scale) }
    }

    fun updateSingleViewScale(scale: Float) {
        viewModelScope.launch { repository.updateSingleViewScale(scale) }
    }

    fun updateColumnVisibility(column: String, visible: Boolean) {
        viewModelScope.launch { 
            repository.updateColumnVisibility(column, visible)
            repository.saveCurrentAsCustom()
        }
    }

    fun moveColumn(column: String, direction: Int) { // -1 for up, 1 for down
        val currentOrder = columnOrder.value.toMutableList()
        val index = currentOrder.indexOf(column)
        if (index == -1) return
        val newIndex = index + direction
        if (newIndex in 0 until currentOrder.size) {
            currentOrder.removeAt(index)
            currentOrder.add(newIndex, column)
            viewModelScope.launch { 
                repository.updateColumnOrder(currentOrder)
                repository.saveCurrentAsCustom()
            }
        }
    }

    fun updateWidgetColumnVisibility(column: String, visible: Boolean) {
        viewModelScope.launch { repository.updateWidgetColumnVisibility(column, visible) }
    }

    fun moveWidgetColumn(column: String, direction: Int) {
        val currentOrder = widgetColumnOrder.value.toMutableList()
        val index = currentOrder.indexOf(column)
        if (index == -1) return
        val newIndex = index + direction
        if (newIndex in 0 until currentOrder.size) {
            currentOrder.removeAt(index)
            currentOrder.add(newIndex, column)
            viewModelScope.launch { repository.updateWidgetColumnOrder(currentOrder) }
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

    fun setNowLineColor(color: Int) {
        viewModelScope.launch { repository.setNowLineColor(color) }
    }

    fun setNowLineThickness(thickness: Float) {
        viewModelScope.launch { repository.setNowLineThickness(thickness) }
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
                    _searchState.value = SearchState.Error(getApplication<Application>().getString(R.string.label_city_not_found))
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

    fun setLanguage(lang: String) {
        viewModelScope.launch { repository.setLanguage(lang) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun clearCache() {
        cacheManager.clearCache()
    }

    fun updateEnabledTithi(tithi: String, enabled: Boolean) {
        viewModelScope.launch { repository.updateEnabledTithi(tithi, enabled) }
    }

    fun updateEnabledNakshatra(star: String, enabled: Boolean) {
        viewModelScope.launch { repository.updateEnabledNakshatra(star, enabled) }
    }

    fun setShowTamilDate(show: Boolean) {
        viewModelScope.launch { repository.setShowTamilDate(show) }
    }

    fun setShowTamilYear(show: Boolean) {
        viewModelScope.launch { repository.setShowTamilYear(show) }
    }

    fun setShowPirai(show: Boolean) {
        viewModelScope.launch { repository.setShowPirai(show) }
    }

    fun setShowSunrise(show: Boolean) {
        viewModelScope.launch { repository.setShowSunrise(show) }
    }

    fun setShowSunset(show: Boolean) {
        viewModelScope.launch { repository.setShowSunset(show) }
    }

    fun setShowBrahmaMuhurtham(show: Boolean) {
        viewModelScope.launch { repository.setShowBrahmaMuhurtham(show) }
    }

    fun setShowAbhijitMuhurtham(show: Boolean) {
        viewModelScope.launch { repository.setShowAbhijitMuhurtham(show) }
    }

    fun setShowMaitraMuhurtham(show: Boolean) {
        viewModelScope.launch { repository.setShowMaitraMuhurtham(show) }
    }

    fun setSunriseDefinition(definition: String) {
        viewModelScope.launch { 
            repository.setSunriseDefinition(definition)
        }
    }

    fun setSpecialPeriodStyle(style: String) {
        viewModelScope.launch { 
            repository.setSpecialPeriodStyle(style)
        }
    }

    fun setLunarMonthSystem(system: String) {
        viewModelScope.launch { 
            repository.setLunarMonthSystem(system)
        }
    }

    fun setTimelineViewStyle(style: String) {
        viewModelScope.launch { 
            repository.setTimelineViewStyle(style)
        }
    }

    fun restoreCustomLayout() {
        viewModelScope.launch { repository.restoreCustomLayout() }
    }

    fun setStandardView(mode: ViewMode) {
        viewModelScope.launch {
            repository.setDefaultLaunchView(mode)
            // When switching to a solo mode via menu, we update the visibility to match
            when (mode) {
                ViewMode.UNIVERSAL -> {
                    listOf("NERAM_MUHURTHAM", "UNIVERSAL", "NERAM", "MAITRA", "BRAHMA", "ABHIJIT", "GOWRI", "HORA").forEach {
                        repository.updateColumnVisibility(it, true)
                    }
                }
                ViewMode.NERAM_MUHURTHAM -> {
                    listOf("NERAM_MUHURTHAM", "UNIVERSAL", "NERAM", "MAITRA", "BRAHMA", "ABHIJIT", "GOWRI", "HORA").forEach {
                        repository.updateColumnVisibility(it, it == "NERAM_MUHURTHAM")
                    }
                }
                ViewMode.NERAM -> {
                    listOf("NERAM_MUHURTHAM", "UNIVERSAL", "NERAM", "MAITRA", "BRAHMA", "ABHIJIT", "GOWRI", "HORA").forEach {
                        repository.updateColumnVisibility(it, it == "NERAM")
                    }
                }
                ViewMode.GOWRI -> {
                    listOf("NERAM_MUHURTHAM", "UNIVERSAL", "NERAM", "MAITRA", "BRAHMA", "ABHIJIT", "GOWRI", "HORA").forEach {
                        repository.updateColumnVisibility(it, it == "GOWRI")
                    }
                }
                ViewMode.HORA -> {
                    listOf("NERAM_MUHURTHAM", "UNIVERSAL", "NERAM", "MAITRA", "BRAHMA", "ABHIJIT", "GOWRI", "HORA").forEach {
                        repository.updateColumnVisibility(it, it == "HORA")
                    }
                }
                ViewMode.MAITRA -> {
                    listOf("NERAM_MUHURTHAM", "UNIVERSAL", "NERAM", "MAITRA", "BRAHMA", "ABHIJIT", "GOWRI", "HORA").forEach {
                        repository.updateColumnVisibility(it, it == "MAITRA")
                    }
                }
                else -> {}
            }
        }
    }
}

sealed interface SearchState {
    data object Idle : SearchState
    data object Searching : SearchState
    data class Results(val addresses: List<Address>) : SearchState
    data class Error(val message: String) : SearchState
}
