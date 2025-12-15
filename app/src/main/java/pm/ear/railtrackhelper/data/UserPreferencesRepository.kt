package pm.ear.railtrackhelper.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val SELECTED_CITY = stringPreferencesKey("selected_city")
        val DISPLAY_LANGUAGE = stringPreferencesKey("display_language")
        val REMEMBER_STATIONS = booleanPreferencesKey("remember_stations")
        val UTILIZE_ALL_RAIL_TYPES = booleanPreferencesKey("utilize_all_rail_types")
        fun favoriteStartStationKey(city: String) = stringPreferencesKey("favorite_start_station_${city}")
        fun favoriteEndStationKey(city: String) = stringPreferencesKey("favorite_end_station_${city}")
        fun lastStartStationKey(city: String) = stringPreferencesKey("last_start_station_${city}")
        fun lastEndStationKey(city: String) = stringPreferencesKey("last_end_station_${city}")
    }

    val selectedCity: Flow<String> = context.dataStore.data
        .map { it[PreferencesKeys.SELECTED_CITY] ?: "Athens" }

    val displayLanguage: Flow<String> = context.dataStore.data
        .map { it[PreferencesKeys.DISPLAY_LANGUAGE] ?: "English" }

    val rememberStations: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.REMEMBER_STATIONS] ?: false }

    val utilizeAllRailTypes: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.UTILIZE_ALL_RAIL_TYPES] ?: false }

    fun favoriteStartStation(city: String): Flow<String?> = context.dataStore.data
        .map { it[PreferencesKeys.favoriteStartStationKey(city)] }

    fun favoriteEndStation(city: String): Flow<String?> = context.dataStore.data
        .map { it[PreferencesKeys.favoriteEndStationKey(city)] }

    fun lastStartStation(city: String): Flow<String?> = context.dataStore.data
        .map { it[PreferencesKeys.lastStartStationKey(city)] }

    fun lastEndStation(city: String): Flow<String?> = context.dataStore.data
        .map { it[PreferencesKeys.lastEndStationKey(city)] }

    suspend fun setRememberStations(remember: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.REMEMBER_STATIONS] = remember }
    }

    suspend fun setUtilizeAllRailTypes(utilize: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.UTILIZE_ALL_RAIL_TYPES] = utilize }
    }

    suspend fun setSelectedCity(city: String) {
        context.dataStore.edit { preferences ->
            if (preferences[PreferencesKeys.SELECTED_CITY] != city) {
                preferences[PreferencesKeys.SELECTED_CITY] = city
            }
        }
    }

    suspend fun setDisplayLanguage(language: String) {
        context.dataStore.edit { it[PreferencesKeys.DISPLAY_LANGUAGE] = language }
    }

    suspend fun setFavoriteStartStation(city: String, stationName: String) {
        context.dataStore.edit { it[PreferencesKeys.favoriteStartStationKey(city)] = stationName }
    }

    suspend fun setFavoriteEndStation(city: String, stationName: String) {
        context.dataStore.edit { it[PreferencesKeys.favoriteEndStationKey(city)] = stationName }
    }

    suspend fun clearFavoriteStartStation(city: String) {
        context.dataStore.edit { it.remove(PreferencesKeys.favoriteStartStationKey(city)) }
    }

    suspend fun clearFavoriteEndStation(city: String) {
        context.dataStore.edit { it.remove(PreferencesKeys.favoriteEndStationKey(city)) }
    }

    suspend fun clearAllFavoriteStations(city: String) {
        context.dataStore.edit {
            it.remove(PreferencesKeys.favoriteStartStationKey(city))
            it.remove(PreferencesKeys.favoriteEndStationKey(city))
        }
    }

    suspend fun setLastUsedStations(city: String, start: String, end: String) {
        context.dataStore.edit {
            it[PreferencesKeys.lastStartStationKey(city)] = start
            it[PreferencesKeys.lastEndStationKey(city)] = end
        }
    }
}