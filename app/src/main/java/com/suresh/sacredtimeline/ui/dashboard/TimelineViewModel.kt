package com.suresh.sacredtimeline.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suresh.sacredtimeline.logic.MockPanchangamProvider
import com.suresh.sacredtimeline.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TimelineViewModel : ViewModel() {
    private val provider = MockPanchangamProvider()
    
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
        _uiState.value = TimelineUiState.Loading
        val timings = provider.getTimings(date)
        
        val nallaNeram = timings.filterIsInstance<NallaNeram>()
        val gowriNeram = timings.filterIsInstance<GowriNeram>()
        val hora = timings.filterIsInstance<Hora>()
        
        _uiState.value = TimelineUiState.Success(
            nallaNeram = nallaNeram,
            gowriNeram = gowriNeram,
            hora = hora
        )
    }
}

sealed interface TimelineUiState {
    object Loading : TimelineUiState
    data class Success(
        val nallaNeram: List<NallaNeram>,
        val gowriNeram: List<GowriNeram>,
        val hora: List<Hora>
    ) : TimelineUiState
}
