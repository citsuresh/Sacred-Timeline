package com.suresh.sacredtimeline.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.suresh.sacredtimeline.logic.MockPanchangamProvider
import com.suresh.sacredtimeline.logic.SunriseSunsetProvider
import com.suresh.sacredtimeline.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

class TimelineViewModel(application: Application) : AndroidViewModel(application) {
    private val provider = MockPanchangamProvider()
    private val sunProvider = SunriseSunsetProvider()
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    
    private var currentLocation: Pair<Double, Double> = Pair(11.0168, 76.9558) // Default to Coimbatore
    private var locationName: String = "Coimbatore"
    private var isLocationAuto: Boolean = false

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _uiState = MutableStateFlow<TimelineUiState>(TimelineUiState.Loading)
    val uiState: StateFlow<TimelineUiState> = _uiState

    private val cachedDays = mutableMapOf<LocalDate, DayData>()

    init {
        viewModelScope.launch {
            selectedDate.collect { date ->
                preloadData(date)
            }
        }
    }

    fun updateDate(date: LocalDate) {
        _selectedDate.value = date
    }

    @SuppressLint("MissingPermission")
    fun onLocationPermissionGranted() {
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
                if (location != null) {
                    currentLocation = Pair(location.latitude, location.longitude)
                    locationName = getAddressFromLocation(location.latitude, location.longitude)
                    isLocationAuto = locationName != "Unknown Location"
                    cachedDays.clear() // Clear cache on location change
                    preloadData(selectedDate.value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getAddressFromLocation(lat: Double, lng: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(getApplication(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].locality ?: addresses[0].subAdminArea ?: "Unknown Location"
                } else {
                    "Unknown Location"
                }
            } catch (e: Exception) {
                "Unknown Location"
            }
        }
    }

    fun updateManualLocation(name: String) {
        locationName = name
        isLocationAuto = false
        cachedDays.clear() // Clear cache on location change
        preloadData(selectedDate.value)
    }

    private fun preloadData(centerDate: LocalDate) {
        viewModelScope.launch {
            // Ensure at least the current date is loading if no data
            if (cachedDays.isEmpty()) {
                _uiState.value = TimelineUiState.Loading
            }

            // Fetch range: -3 to +3 days
            val datesToLoad = (-3..3).map { centerDate.plusDays(it.toLong()) }
            
            datesToLoad.forEach { date ->
                if (!cachedDays.containsKey(date)) {
                    val dayData = fetchDayData(date)
                    cachedDays[date] = dayData
                }
            }

            _uiState.value = TimelineUiState.Success(
                days = cachedDays.toMap(),
                locationName = locationName,
                isLocationAuto = isLocationAuto
            )
        }
    }

    private suspend fun fetchDayData(date: LocalDate): DayData {
        val (lat, lng) = currentLocation
        val sunResult = sunProvider.getSunTimes(lat, lng, date)
        val timings = provider.getTimings(date, sunResult.sunrise, sunResult.sunset)
        
        return DayData(
            nallaNeram = timings.filterIsInstance<NallaNeram>(),
            gowriNeram = timings.filterIsInstance<GowriNeram>(),
            hora = timings.filterIsInstance<Hora>(),
            specialPeriods = timings.filterIsInstance<SpecialPeriod>(),
            sunrise = sunResult.sunrise,
            sunset = sunResult.sunset,
            isFallback = sunResult.isFallback
        )
    }
}

data class DayData(
    val nallaNeram: List<NallaNeram>,
    val gowriNeram: List<GowriNeram>,
    val hora: List<Hora>,
    val specialPeriods: List<SpecialPeriod>,
    val sunrise: LocalTime,
    val sunset: LocalTime,
    val isFallback: Boolean
)

sealed interface TimelineUiState {
    data object Loading : TimelineUiState
    data class Success(
        val days: Map<LocalDate, DayData>,
        val locationName: String,
        val isLocationAuto: Boolean
    ) : TimelineUiState
}
