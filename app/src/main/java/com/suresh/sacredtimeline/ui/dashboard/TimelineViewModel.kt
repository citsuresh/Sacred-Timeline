package com.suresh.sacredtimeline.ui.dashboard

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.suresh.sacredtimeline.data.CacheManager
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.logic.MockPanchangamProvider
import com.suresh.sacredtimeline.logic.SunriseSunsetProvider
import com.suresh.sacredtimeline.model.*
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.util.Locale

class TimelineViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)
    private val cacheManager = CacheManager(application)
    private val provider = MockPanchangamProvider()
    private val sunProvider = SunriseSunsetProvider()
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    
    private var currentLocation: Pair<Double, Double> = Pair(11.0168, 76.9558) // Default to Coimbatore
    private val _locationName = MutableStateFlow("Coimbatore")
    private val _isLocationAuto = MutableStateFlow(false)

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _uiState = MutableStateFlow<TimelineUiState>(TimelineUiState.Loading)
    val uiState: StateFlow<TimelineUiState> = _uiState

    private val _viewMode = MutableStateFlow(ViewMode.COMPOSITE)
    
    private val _timelineScale = MutableStateFlow(1.0f)
    val timelineScale: StateFlow<Float> = _timelineScale

    private var refreshJob: kotlinx.coroutines.Job? = null
    private val cachedDays = mutableMapOf<LocalDate, DayData>()
    private val cacheMutex = Mutex()

    val timeFormat24h = repository.timeFormat24h
    
    val showNowLine: StateFlow<Boolean> = repository.showNowLine.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val nowLineColor: StateFlow<Int> = repository.nowLineColor.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF4CAF50.toInt()
    )

    val nowLineThickness: StateFlow<Float> = repository.nowLineThickness.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 2.0f
    )
    
    val pinchToZoomEnabled = repository.pinchToZoomEnabled

    val columnVisibility: StateFlow<Set<String>> = repository.columnVisibility.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("UNIVERSAL")
    )

    val columnOrder: StateFlow<List<String>> = repository.columnOrder.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("NERAM_MUHURTHAM", "UNIVERSAL", "NERAM", "BRAHMA", "ABHIJIT", "GOWRI", "HORA")
    )

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

    val timelineViewStyle: StateFlow<String> = repository.timelineViewStyle.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "EQUAL_DISTRIBUTION"
    )

    init {
        // Clear cache to ensure new timing structure and filters are applied
        cacheManager.clearCache()

        // Observe scale settings based on view mode
        viewModelScope.launch {
            combine(repository.compositeScale, repository.singleViewScale, _viewMode) { composite, single, mode ->
                if (mode == ViewMode.COMPOSITE) composite else single
            }.collect {
                _timelineScale.value = it
            }
        }

        // Observe Location Settings
        viewModelScope.launch {
            combine(
                repository.locationMode, 
                repository.manualCityName,
                repository.manualLatitude,
                repository.manualLongitude
            ) { mode, city, lat, lng ->
                Triple(mode, city, lat to lng)
            }.collect { (mode, city, coords) ->
                val hasChanged = if (mode == "MANUAL") {
                    _locationName.value != city || currentLocation != coords || _isLocationAuto.value
                } else {
                    false 
                }

                if (mode == "MANUAL") {
                    _locationName.value = city
                    _isLocationAuto.value = false
                    currentLocation = coords
                    
                    if (hasChanged) {
                        cacheMutex.withLock { cachedDays.clear() }
                        preloadData(selectedDate.value, repository.preloadDays.first())
                    } else if (cachedDays.isEmpty()) {
                        // Cold start in Manual Mode: Try to load from disk
                        val diskData = cacheManager.loadCache(coords.first, coords.second)
                        if (diskData != null) {
                            cacheMutex.withLock {
                                cachedDays.putAll(diskData)
                            }
                            updateSuccessState()
                        }
                        // Refresh/Preload silently
                        preloadData(selectedDate.value, repository.preloadDays.first())
                    }
                } else if (mode == "AUTO") {
                    onLocationPermissionGranted()
                }
            }
        }

        // Observe date changes and preload
        viewModelScope.launch {
            combine(selectedDate, repository.preloadDays) { date, days ->
                date to days
            }.collect { (date, days) ->
                refreshJob?.cancel()
                refreshJob = viewModelScope.launch {
                    preloadData(date, days)
                }
            }
        }

        // Observe settings changes and clear cache to force re-calculation of filtered IDs and new precision
        viewModelScope.launch {
            combine(
                repository.enabledTithis, 
                repository.enabledNakshatras,
                repository.sunriseDefinition,
                repository.specialPeriodStyle,
                repository.lunarMonthSystem,
                repository.preloadDays
            ) { values ->
                // Use array for combine > 5 flows
                DataRefreshTrigger(
                    tithis = values[0] as Set<String>,
                    stars = values[1] as Set<String>,
                    sunDef = values[2] as String,
                    style = values[3] as String,
                    system = values[4] as String,
                    range = values[5] as Int
                )
            }.collect { trigger ->
                refreshJob?.cancel()
                refreshJob = viewModelScope.launch {
                    cacheMutex.withLock { cachedDays.clear() }
                    cacheManager.clearCache() // Clear disk too
                    preloadData(selectedDate.value, trigger.range)
                }
            }
        }
    }

    private data class DataRefreshTrigger(
        val tithis: Set<String>,
        val stars: Set<String>,
        val sunDef: String,
        val style: String,
        val system: String,
        val range: Int
    )

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun updateDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun updateTimelineScale(scale: Float) {
        viewModelScope.launch {
            if (_viewMode.value == ViewMode.COMPOSITE) {
                repository.updateCompositeScale(scale)
            } else {
                repository.updateSingleViewScale(scale)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun onLocationPermissionGranted() {
        viewModelScope.launch {
            try {
                // Only act if mode is AUTO
                val mode = repository.locationMode.first()
                if (mode != "AUTO") return@launch

                val location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
                if (location != null) {
                    val newCoords = Pair(location.latitude, location.longitude)
                    
                    val distanceMoved = Math.abs(currentLocation.first - newCoords.first) + 
                                      Math.abs(currentLocation.second - newCoords.second)
                    
                    if (distanceMoved > 0.001 || !_isLocationAuto.value) {
                        currentLocation = newCoords
                        repository.updateLastKnownCoordinates(newCoords.first, newCoords.second)
                        
                        val name = getAddressFromLocation(location.latitude, location.longitude)
                        _locationName.value = name
                        _isLocationAuto.value = name != "Unknown Location"
                        
                        refreshJob?.cancel()
                        refreshJob = viewModelScope.launch {
                            val diskData = cacheManager.loadCache(newCoords.first, newCoords.second)
                            cacheMutex.withLock {
                                cachedDays.clear()
                                if (diskData != null) cachedDays.putAll(diskData)
                            }
                            if (diskData != null) updateSuccessState()
                            preloadData(selectedDate.value, repository.preloadDays.first())
                        }
                    } else if (cachedDays.isEmpty()) {
                        refreshJob?.cancel()
                        refreshJob = viewModelScope.launch {
                            val diskData = cacheManager.loadCache(newCoords.first, newCoords.second)
                            if (diskData != null) {
                                cacheMutex.withLock { cachedDays.putAll(diskData) }
                                updateSuccessState()
                            }
                            preloadData(selectedDate.value, repository.preloadDays.first())
                        }
                    }
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
        viewModelScope.launch {
            repository.setLocationMode("MANUAL")
            repository.setManualCityName(name)
        }
    }

    private fun updateSuccessState() {
        viewModelScope.launch {
            val snapshot = cacheMutex.withLock { cachedDays.toMap() }
            _uiState.value = TimelineUiState.Success(
                days = snapshot,
                locationName = _locationName.value,
                isLocationAuto = _isLocationAuto.value
            )
        }
    }

    private suspend fun preloadData(centerDate: LocalDate, rangeDays: Int = 3) {
        // Ensure at least the current date is loading if no data
        val isEmpty = cacheMutex.withLock { cachedDays.isEmpty() }
        if (isEmpty) {
            _uiState.value = TimelineUiState.Loading
        }

        // Fetch range: -rangeDays to +rangeDays
        val datesToLoad = (-rangeDays..rangeDays).map { centerDate.plusDays(it.toLong()) }
        
        datesToLoad.forEach { date ->
            val alreadyCached = cacheMutex.withLock { cachedDays.containsKey(date) }
            if (!alreadyCached) {
                val dayData = fetchDayData(date)
                cacheMutex.withLock { cachedDays[date] = dayData }
            }
        }

        val snapshot = cacheMutex.withLock { cachedDays.toMap() }
        _uiState.value = TimelineUiState.Success(
            days = snapshot,
            locationName = _locationName.value,
            isLocationAuto = _isLocationAuto.value
        )
        
        // Save to disk
        cacheManager.saveCache(currentLocation.first, currentLocation.second, snapshot)
    }

    private suspend fun fetchDayData(date: LocalDate): DayData {
        val (lat, lng) = currentLocation
        val sunDef = repository.sunriseDefinition.first()
        val style = repository.specialPeriodStyle.first()

        val sunResult = sunProvider.getSunTimes(lat, lng, date, sunDef)
        val timings = provider.getTimings(date, sunResult.sunrise, sunResult.sunset, style, sunDef, lat, lng)
        
        val tamilCalendar = com.suresh.sacredtimeline.logic.TamilCalendarUtils.getTamilDate(date)
        val lunarDayInfo = com.suresh.sacredtimeline.logic.LunarCalendarUtils.getLunarDayInfo(date)
        
        // Aggregate festivals from all Tithis and Nakshatras occurring during the day (Part 2: Interval Wiring)
        val festivalSet = mutableSetOf<Int>()
        lunarDayInfo.tithis.forEach { tithiInterval ->
            lunarDayInfo.nakshatras.forEach { nakshatraInterval ->
                val info = com.suresh.sacredtimeline.logic.LunarCalendarUtils.LunarInfo(
                    tithi = tithiInterval.value,
                    nakshatra = nakshatraInterval.value,
                    pakshaResId = lunarDayInfo.pakshaResId,
                    pakshaDay = lunarDayInfo.pakshaDay,
                    tithiResId = tithiInterval.resId,
                    nakshatraResId = nakshatraInterval.resId
                )
                festivalSet.addAll(com.suresh.sacredtimeline.logic.TamilCalendarUtils.getSpecialEvents(tamilCalendar, info))
            }
        }
        val festivals = festivalSet.toList()
        val holidays = com.suresh.sacredtimeline.data.VerifiedHolidays.getHolidays(date)
        val combinedEvents = (holidays + festivals).distinct()
        
        val brahmaTimes = com.suresh.sacredtimeline.logic.LunarCalendarUtils.calculateBrahmaMuhurtham(sunResult.sunrise)
        val brahma = Muhurtham(
            name = "Brahma Muhurtham",
            tamilName = "",
            startTime = brahmaTimes.first,
            endTime = brahmaTimes.second,
            auspiciousness = Auspiciousness.GREEN,
            description = ""
        )

        val abhijitTimes = com.suresh.sacredtimeline.logic.LunarCalendarUtils.calculateAbhijitMuhurtham(sunResult.sunrise, sunResult.sunset)
        val abhijit = abhijitTimes?.let {
            Muhurtham(
                name = "Abhijit Muhurtham",
                tamilName = "",
                startTime = it.first,
                endTime = it.second,
                auspiciousness = Auspiciousness.GREEN,
                description = ""
            )
        }

        val maitra = com.suresh.sacredtimeline.logic.PanchangamCalculator.calculateMaitraMuhurtham(
            date, lat, lng, sunResult.sunrise
        )

        // Filter Tithi and Nakshatra based on user preferences
        val enabledTithisVal = repository.enabledTithis.first()
        val enabledStarsVal = repository.enabledNakshatras.first()
        
        val filteredTithis = lunarDayInfo.tithis.filter { interval ->
            val normalizedValue = if (interval.value > 15) interval.value - 15 else interval.value
            enabledTithisVal.contains("TITHI_${interval.value}") || 
            (interval.value > 15 && enabledTithisVal.contains("TITHI_$normalizedValue"))
        }.map { 
            LunarInterval(it.value, it.resId, it.startTime, it.endTime)
        }

        val filteredNakshatras = lunarDayInfo.nakshatras.filter { interval ->
            enabledStarsVal.contains("STAR_${interval.value}") 
        }.map { 
            LunarInterval(it.value, it.resId, it.startTime, it.endTime)
        }

        return DayData(
            nallaNeram = timings.filterIsInstance<NallaNeram>(),
            gowriNeram = timings.filterIsInstance<GowriNeram>(),
            hora = timings.filterIsInstance<Hora>(),
            specialPeriods = timings.filterIsInstance<SpecialPeriod>(),
            sunrise = sunResult.sunrise,
            sunset = sunResult.sunset,
            isFallback = sunResult.isFallback,
            tamilDay = tamilCalendar.day,
            tamilMonthResId = tamilCalendar.monthResId,
            tamilYearResId = tamilCalendar.yearResId,
            pakshaResId = lunarDayInfo.pakshaResId,
            pakshaDay = lunarDayInfo.pakshaDay,
            tithis = filteredTithis,
            nakshatras = filteredNakshatras,
            specialEvents = combinedEvents,
            isSubhaMuhurtham = com.suresh.sacredtimeline.data.VerifiedHolidays.isSubhaMuhurtham(date),
            brahmaMuhurtham = brahma,
            abhijitMuhurtham = abhijit,
            maitraMuhurtham = maitra
        )
    }
}

sealed interface TimelineUiState {
    data object Loading : TimelineUiState
    data class Success(
        val days: Map<LocalDate, DayData>,
        val locationName: String,
        val isLocationAuto: Boolean
    ) : TimelineUiState
}
