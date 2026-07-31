package com.suresh.sacredtimeline.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suresh.sacredtimeline.logic.MockPanchangamProvider
import com.suresh.sacredtimeline.logic.SunriseSunsetProvider
import com.suresh.sacredtimeline.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class TimelineViewModel : ViewModel() {
    private val provider = MockPanchangamProvider()
    private val sunProvider = SunriseSunsetProvider()
    
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

    private fun loadData(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = TimelineUiState.Loading
            
            // Hardcoded location for now, can be expanded to use FusedLocationProvider
            val lat = 11.0168 
            val lng = 76.9558
            
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
                isFallback = sunResult.isFallback
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
        val isFallback: Boolean
    ) : TimelineUiState
}
