package com.weatherender.widget.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "weatherender_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

object WidgetPreferences {
    fun cityKey(widgetId: Int): Preferences.Key<String> = stringPreferencesKey("city_$widgetId")

    suspend fun saveCity(context: Context, widgetId: Int, city: String) {
        context.dataStore.edit { prefs ->
            prefs[cityKey(widgetId)] = city
        }
    }

    suspend fun getCity(context: Context, widgetId: Int): String? {
        return context.dataStore.data
            .map { prefs -> prefs[cityKey(widgetId)] }
            .firstOrNull()
    }
}

object AppPreferences {
    private val appCityKey = stringPreferencesKey("app_city")
    private val appUnitMetricKey = booleanPreferencesKey("app_unit_metric")
    private val lastUpdatedKey = longPreferencesKey("last_updated_epoch")

    suspend fun saveCity(context: Context, city: String) {
        context.dataStore.edit { prefs ->
            prefs[appCityKey] = city
        }
    }

    suspend fun getCity(context: Context): String? {
        return context.dataStore.data
            .map { prefs -> prefs[appCityKey] }
            .firstOrNull()
    }

    suspend fun saveUnitMetric(context: Context, isMetric: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[appUnitMetricKey] = isMetric
        }
    }

    suspend fun isMetric(context: Context): Boolean {
        return context.dataStore.data
            .map { prefs -> prefs[appUnitMetricKey] ?: true }
            .firstOrNull() ?: true
    }

    suspend fun saveLastUpdated(context: Context, epochMillis: Long) {
        context.dataStore.edit { prefs ->
            prefs[lastUpdatedKey] = epochMillis
        }
    }

    suspend fun getLastUpdated(context: Context): Long? {
        return context.dataStore.data
            .map { prefs -> prefs[lastUpdatedKey] }
            .firstOrNull()
    }
}
