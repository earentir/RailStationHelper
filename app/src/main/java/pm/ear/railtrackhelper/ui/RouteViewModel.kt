package pm.ear.railtrackhelper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pm.ear.railtrackhelper.data.City
import pm.ear.railtrackhelper.data.MetroData
import pm.ear.railtrackhelper.data.Station
import pm.ear.railtrackhelper.data.UserPreferencesRepository
import pm.ear.railtrackhelper.domain.RouteFinder
import pm.ear.railtrackhelper.domain.RouteSegment

class RouteViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    private val routeFinder = RouteFinder()

    private val _snackbarChannel = Channel<String>()
    val snackbarMessage = _snackbarChannel.receiveAsFlow()

    val selectedCity: StateFlow<City> = userPreferencesRepository.selectedCity
        .map { cityName -> MetroData.cities.find { it.name == cityName } ?: MetroData.cities.first() }
        .stateIn(viewModelScope, SharingStarted.Lazily, MetroData.cities.first())

    val displayLanguage: StateFlow<String> = userPreferencesRepository.displayLanguage
        .stateIn(viewModelScope, SharingStarted.Lazily, "English")

    val rememberStations: StateFlow<Boolean> = userPreferencesRepository.rememberStations
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _startStationQuery = MutableStateFlow("")
    val startStationQuery: StateFlow<String> = _startStationQuery

    private val _endStationQuery = MutableStateFlow("")
    val endStationQuery: StateFlow<String> = _endStationQuery

    private val _favoriteStationQuery = MutableStateFlow("")
    val favoriteStationQuery: StateFlow<String> = _favoriteStationQuery

    private val _routeResult = MutableStateFlow<RouteResult>(RouteResult.Empty)
    val routeResult: StateFlow<RouteResult> = _routeResult

    val startStationSuggestions: StateFlow<List<Station>> = combine(
        _startStationQuery, selectedCity
    ) { query, city ->
        if (query.isBlank()) {
            city.stations
        } else {
            city.stations.filter {
                it.nameEn.contains(query, ignoreCase = true) || it.nameEl.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val endStationSuggestions: StateFlow<List<Station>> = combine(
        _endStationQuery, selectedCity
    ) { query, city ->
        if (query.isBlank()) {
            city.stations
        } else {
            city.stations.filter {
                it.nameEn.contains(query, ignoreCase = true) || it.nameEl.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteStationSuggestions: StateFlow<List<Station>> = combine(
        _favoriteStationQuery, selectedCity
    ) { query, city ->
        if (query.isBlank()) {
            city.stations
        } else {
            city.stations.filter {
                it.nameEn.contains(query, ignoreCase = true) || it.nameEl.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteStartStation: StateFlow<Station?> = selectedCity.flatMapLatest { city ->
        userPreferencesRepository.favoriteStartStation(city.name).map { stationName ->
            city.stations.find { it.nameEn == stationName }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val favoriteEndStation: StateFlow<Station?> = selectedCity.flatMapLatest { city ->
        userPreferencesRepository.favoriteEndStation(city.name).map { stationName ->
            city.stations.find { it.nameEn == stationName }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val lastStartStation: StateFlow<Station?> = selectedCity.flatMapLatest { city ->
        userPreferencesRepository.lastStartStation(city.name).map { stationName ->
            city.stations.find { it.nameEn == stationName }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val lastEndStation: StateFlow<Station?> = selectedCity.flatMapLatest { city ->
        userPreferencesRepository.lastEndStation(city.name).map { stationName ->
            city.stations.find { it.nameEn == stationName }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onStartStationQueryChange(query: String) {
        _startStationQuery.value = query
    }

    fun onEndStationQueryChange(query: String) {
        _endStationQuery.value = query
    }

    fun onFavoriteStationQueryChange(query: String) {
        _favoriteStationQuery.value = query
    }

    fun findRoute(startStation: Station, endStation: Station) {
        viewModelScope.launch {
            val result = routeFinder.findRoute(selectedCity.value, startStation, endStation)
            if (result != null) {
                _routeResult.value = RouteResult.Success(result)
                if (rememberStations.value) {
                    userPreferencesRepository.setLastUsedStations(selectedCity.value.name, startStation.nameEn, endStation.nameEn)
                }
            } else {
                _routeResult.value = RouteResult.Error("No route found")
            }
        }
    }

    fun setSelectedCity(cityName: String) {
        viewModelScope.launch {
            userPreferencesRepository.setSelectedCity(cityName)
        }
    }

    fun setDisplayLanguage(language: String) {
        viewModelScope.launch {
            userPreferencesRepository.setDisplayLanguage(language)
        }
    }

    fun setFavoriteStartStation(station: Station) {
        viewModelScope.launch {
            userPreferencesRepository.setFavoriteStartStation(selectedCity.value.name, station.nameEn)
            _snackbarChannel.send("Set ${getStationName(station)} as favorite start station.")
        }
    }

    fun setFavoriteEndStation(station: Station) {
        viewModelScope.launch {
            userPreferencesRepository.setFavoriteEndStation(selectedCity.value.name, station.nameEn)
            _snackbarChannel.send("Set ${getStationName(station)} as favorite end station.")
        }
    }

    fun clearFavoriteStartStation() {
        viewModelScope.launch {
            userPreferencesRepository.clearFavoriteStartStation(selectedCity.value.name)
            _snackbarChannel.send("Favorite start station cleared.")
        }
    }

    fun clearFavoriteEndStation() {
        viewModelScope.launch {
            userPreferencesRepository.clearFavoriteEndStation(selectedCity.value.name)
            _snackbarChannel.send("Favorite end station cleared.")
        }
    }

    fun clearAllFavoriteStations() {
        viewModelScope.launch {
            userPreferencesRepository.clearAllFavoriteStations(selectedCity.value.name)
            _snackbarChannel.send("All favorite stations for ${selectedCity.value.name} cleared.")
        }
    }

    fun setRememberStations(remember: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setRememberStations(remember)
        }
    }

    fun getStationName(station: Station): String {
        return if (displayLanguage.value == "Native") station.nameEl else station.nameEn
    }

    fun clearStartStation() {
        _startStationQuery.value = ""
    }

    fun clearEndStation() {
        _endStationQuery.value = ""
    }
}

sealed class RouteResult {
    object Empty : RouteResult()
    data class Success(val path: List<RouteSegment>) : RouteResult()
    data class Error(val message: String) : RouteResult()
}
