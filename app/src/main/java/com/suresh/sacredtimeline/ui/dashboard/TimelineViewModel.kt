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

    init {
        viewModelScope.launch {
            selectedDate.collect { date ->
                loadData(date)
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
                    loadData(selectedDate.value)
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
        loadData(selectedDate.value)
    }

    private fun loadData(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = TimelineUiState.Loading
            
            val (lat, lng) = currentLocation
            
            val sunResult = sunProvider.getSunTimes(lat, lng, date)
            val timings = provider.getTimings(date, sunResult.sunrise, sunResult.sunset)
            
            val nallaNeram = timings.filterIsInstance<NallaNeram>()
            val gowriNeram = timings.filterIsInstance<GowriNeram>()
            val hora = timings.filterIsInstance<Hora>()
            val specialPeriods = timings.filterIsInstance<SpecialPeriod>()
            
            _uiState.value = TimelineUiState.Success(
                nallaNeram = nallaNeram,
                gowriNeram = gowriNeram,
                hora = hora,
                specialPeriods = specialPeriods,
                sunrise = sunResult.sunrise,
                sunset = sunResult.sunset,
                isFallback = sunResult.isFallback,
                locationName = locationName,
                isLocationAuto = isLocationAuto
            )
        }
    }
}

sealed interface TimelineUiState {
    data object Loading : TimelineUiState
    data class Success(
        val nallaNeram: List<NallaNeram>,
        val gowriNeram: List<GowriNeram>,
        val hora: List<Hora>,
        val specialPeriods: List<SpecialPeriod>,
        val sunrise: LocalTime,
        val sunset: LocalTime,
        val isFallback: Boolean,
        val locationName: String,
        val isLocationAuto: Boolean
    ) : TimelineUiState
}
