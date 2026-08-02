package com.suresh.sacredtimeline.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.suresh.sacredtimeline.ui.navigation.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val COMPOSITE_SCALE = floatPreferencesKey("composite_scale")
        val SINGLE_VIEW_SCALE = floatPreferencesKey("single_view_scale")
        val COLUMN_VISIBILITY = stringSetPreferencesKey("column_visibility")
        val COLUMN_ORDER = stringPreferencesKey("column_order")
        val WIDGET_COLUMN_VISIBILITY = stringSetPreferencesKey("widget_column_visibility")
        val WIDGET_COLUMN_ORDER = stringPreferencesKey("widget_column_order")
        val DEFAULT_LAUNCH_VIEW = stringPreferencesKey("default_launch_view")
        val TIME_FORMAT_24H = booleanPreferencesKey("time_format_24h")
        val SHOW_NOW_LINE = booleanPreferencesKey("show_now_line")
        val NOW_LINE_COLOR = intPreferencesKey("now_line_color")
        val NOW_LINE_THICKNESS = floatPreferencesKey("now_line_thickness")
        val PINCH_TO_ZOOM_ENABLED = booleanPreferencesKey("pinch_to_zoom_enabled")
        val WIDGET_REFRESH_MINUTES = intPreferencesKey("widget_refresh_minutes")
        val LOCATION_MODE = stringPreferencesKey("location_mode")
        val MANUAL_CITY_NAME = stringPreferencesKey("manual_city_name")
        val MANUAL_LATITUDE = doublePreferencesKey("manual_latitude")
        val MANUAL_LONGITUDE = doublePreferencesKey("manual_longitude")
        val LAST_KNOWN_LATITUDE = doublePreferencesKey("last_known_latitude")
        val LAST_KNOWN_LONGITUDE = doublePreferencesKey("last_known_longitude")
        val PRELOAD_DAYS = intPreferencesKey("preload_days")
    }

    val compositeScale: Flow<Float> = context.dataStore.data.map { it[Keys.COMPOSITE_SCALE] ?: 1.0f }
    val singleViewScale: Flow<Float> = context.dataStore.data.map { it[Keys.SINGLE_VIEW_SCALE] ?: 0.5f }
    
    val columnVisibility: Flow<Set<String>> = context.dataStore.data.map { 
        it[Keys.COLUMN_VISIBILITY] ?: setOf("NERAM", "GOWRI", "HORA")
    }

    val columnOrder: Flow<List<String>> = context.dataStore.data.map { 
        val orderString = it[Keys.COLUMN_ORDER] ?: "NERAM,GOWRI,HORA"
        orderString.split(",").filter { it.isNotBlank() }
    }

    val widgetColumnVisibility: Flow<Set<String>> = context.dataStore.data.map { 
        it[Keys.WIDGET_COLUMN_VISIBILITY] ?: setOf("NERAM", "GOWRI", "HORA")
    }

    val widgetColumnOrder: Flow<List<String>> = context.dataStore.data.map { 
        val orderString = it[Keys.WIDGET_COLUMN_ORDER] ?: "NERAM,GOWRI,HORA"
        orderString.split(",").filter { it.isNotBlank() }
    }

    val defaultLaunchView: Flow<ViewMode> = context.dataStore.data.map { 
        val name = it[Keys.DEFAULT_LAUNCH_VIEW] ?: ViewMode.COMPOSITE.name
        try { ViewMode.valueOf(name) } catch (e: Exception) { ViewMode.COMPOSITE }
    }

    val timeFormat24h: Flow<Boolean> = context.dataStore.data.map { it[Keys.TIME_FORMAT_24H] ?: false }
    val showNowLine: Flow<Boolean> = context.dataStore.data.map { it[Keys.SHOW_NOW_LINE] ?: true }
    val nowLineColor: Flow<Int> = context.dataStore.data.map { it[Keys.NOW_LINE_COLOR] ?: 0xFFFF0000.toInt() } // Default Red
    val nowLineThickness: Flow<Float> = context.dataStore.data.map { it[Keys.NOW_LINE_THICKNESS] ?: 2.0f }
    val pinchToZoomEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.PINCH_TO_ZOOM_ENABLED] ?: true }

    val locationMode: Flow<String> = context.dataStore.data.map { it[Keys.LOCATION_MODE] ?: "AUTO" }
    val manualCityName: Flow<String> = context.dataStore.data.map { it[Keys.MANUAL_CITY_NAME] ?: "Coimbatore" }
    val manualLatitude: Flow<Double> = context.dataStore.data.map { it[Keys.MANUAL_LATITUDE] ?: 11.0168 }
    val manualLongitude: Flow<Double> = context.dataStore.data.map { it[Keys.MANUAL_LONGITUDE] ?: 76.9558 }

    val lastKnownLatitude: Flow<Double> = context.dataStore.data.map { it[Keys.LAST_KNOWN_LATITUDE] ?: 11.0168 }
    val lastKnownLongitude: Flow<Double> = context.dataStore.data.map { it[Keys.LAST_KNOWN_LONGITUDE] ?: 76.9558 }

    val widgetRefreshMinutes: Flow<Int> = context.dataStore.data.map { it[Keys.WIDGET_REFRESH_MINUTES] ?: 30 }
    
    val preloadDays: Flow<Int> = context.dataStore.data.map { it[Keys.PRELOAD_DAYS] ?: 3 }

    suspend fun updateCompositeScale(scale: Float) {
        context.dataStore.edit { it[Keys.COMPOSITE_SCALE] = scale.coerceIn(0.2f, 3.0f) }
    }

    suspend fun updateSingleViewScale(scale: Float) {
        context.dataStore.edit { it[Keys.SINGLE_VIEW_SCALE] = scale.coerceIn(0.2f, 3.0f) }
    }

    suspend fun updateColumnVisibility(column: String, visible: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.COLUMN_VISIBILITY] ?: setOf("NERAM", "GOWRI", "HORA")
            val newSet = current.toMutableSet()
            if (visible) newSet.add(column) else newSet.remove(column)
            prefs[Keys.COLUMN_VISIBILITY] = newSet
        }
    }

    suspend fun updateColumnOrder(order: List<String>) {
        context.dataStore.edit { it[Keys.COLUMN_ORDER] = order.joinToString(",") }
    }

    suspend fun updateWidgetColumnVisibility(column: String, visible: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.WIDGET_COLUMN_VISIBILITY] ?: setOf("NERAM", "GOWRI", "HORA")
            val newSet = current.toMutableSet()
            if (visible) newSet.add(column) else newSet.remove(column)
            prefs[Keys.WIDGET_COLUMN_VISIBILITY] = newSet
        }
    }

    suspend fun updateWidgetColumnOrder(order: List<String>) {
        context.dataStore.edit { it[Keys.WIDGET_COLUMN_ORDER] = order.joinToString(",") }
    }

    suspend fun setDefaultLaunchView(mode: ViewMode) {
        context.dataStore.edit { it[Keys.DEFAULT_LAUNCH_VIEW] = mode.name }
    }

    suspend fun setTimeFormat24h(is24h: Boolean) {
        context.dataStore.edit { it[Keys.TIME_FORMAT_24H] = is24h }
    }

    suspend fun setShowNowLine(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_NOW_LINE] = show }
    }

    suspend fun setNowLineColor(color: Int) {
        context.dataStore.edit { it[Keys.NOW_LINE_COLOR] = color }
    }

    suspend fun setNowLineThickness(thickness: Float) {
        context.dataStore.edit { it[Keys.NOW_LINE_THICKNESS] = thickness }
    }

    suspend fun setPinchToZoomEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PINCH_TO_ZOOM_ENABLED] = enabled }
    }

    suspend fun setLocationMode(mode: String) {
        context.dataStore.edit { it[Keys.LOCATION_MODE] = mode }
    }

    suspend fun setManualCityName(name: String) {
        context.dataStore.edit { it[Keys.MANUAL_CITY_NAME] = name }
    }

    suspend fun updateManualCoordinates(lat: Double, lng: Double) {
        context.dataStore.edit { 
            it[Keys.MANUAL_LATITUDE] = lat
            it[Keys.MANUAL_LONGITUDE] = lng
        }
    }

    suspend fun updateLastKnownCoordinates(lat: Double, lng: Double) {
        context.dataStore.edit { 
            it[Keys.LAST_KNOWN_LATITUDE] = lat
            it[Keys.LAST_KNOWN_LONGITUDE] = lng
        }
    }

    suspend fun setWidgetRefreshMinutes(minutes: Int) {
        context.dataStore.edit { it[Keys.WIDGET_REFRESH_MINUTES] = minutes }
    }

    suspend fun setPreloadDays(days: Int) {
        context.dataStore.edit { it[Keys.PRELOAD_DAYS] = days }
    }
}
