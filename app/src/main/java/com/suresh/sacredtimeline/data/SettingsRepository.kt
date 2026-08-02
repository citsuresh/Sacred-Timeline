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
        val DEFAULT_LAUNCH_VIEW = stringPreferencesKey("default_launch_view")
        val TIME_FORMAT_24H = booleanPreferencesKey("time_format_24h")
        val SHOW_NOW_LINE = booleanPreferencesKey("show_now_line")
        val PINCH_TO_ZOOM_ENABLED = booleanPreferencesKey("pinch_to_zoom_enabled")
        val NOW_LINE_COLOR = intPreferencesKey("now_line_color")
        val WIDGET_REFRESH_MINUTES = intPreferencesKey("widget_refresh_minutes")
        val LOCATION_MODE = stringPreferencesKey("location_mode")
        val MANUAL_CITY_NAME = stringPreferencesKey("manual_city_name")
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

    val defaultLaunchView: Flow<ViewMode> = context.dataStore.data.map { 
        val name = it[Keys.DEFAULT_LAUNCH_VIEW] ?: ViewMode.COMPOSITE.name
        try { ViewMode.valueOf(name) } catch (e: Exception) { ViewMode.COMPOSITE }
    }

    val timeFormat24h: Flow<Boolean> = context.dataStore.data.map { it[Keys.TIME_FORMAT_24H] ?: false }
    val showNowLine: Flow<Boolean> = context.dataStore.data.map { it[Keys.SHOW_NOW_LINE] ?: true }
    val pinchToZoomEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.PINCH_TO_ZOOM_ENABLED] ?: true }

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

    suspend fun setDefaultLaunchView(mode: ViewMode) {
        context.dataStore.edit { it[Keys.DEFAULT_LAUNCH_VIEW] = mode.name }
    }

    suspend fun setTimeFormat24h(is24h: Boolean) {
        context.dataStore.edit { it[Keys.TIME_FORMAT_24H] = is24h }
    }

    suspend fun setShowNowLine(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_NOW_LINE] = show }
    }

    suspend fun setPinchToZoomEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PINCH_TO_ZOOM_ENABLED] = enabled }
    }
}
