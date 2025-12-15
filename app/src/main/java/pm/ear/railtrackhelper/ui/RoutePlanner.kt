package pm.ear.railtrackhelper.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pm.ear.railtrackhelper.data.Station

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RoutePlannerScreen(
    modifier: Modifier = Modifier,
    routeViewModel: RouteViewModel = viewModel()
) {
    var startStation by remember { mutableStateOf<Station?>(null) }
    var endStation by remember { mutableStateOf<Station?>(null) }
    val routeResult by routeViewModel.routeResult.collectAsState()
    val favoriteStartStation by routeViewModel.favoriteStartStation.collectAsState()
    val favoriteEndStation by routeViewModel.favoriteEndStation.collectAsState()
    val lastStartStation by routeViewModel.lastStartStation.collectAsState()
    val lastEndStation by routeViewModel.lastEndStation.collectAsState()
    val rememberStations by routeViewModel.rememberStations.collectAsState()
    val selectedCity by routeViewModel.selectedCity.collectAsState()

    LaunchedEffect(selectedCity) {
        routeViewModel.clearStartStation()
        routeViewModel.clearEndStation()
        startStation = null
        endStation = null
    }

    LaunchedEffect(favoriteStartStation, favoriteEndStation, lastStartStation, lastEndStation, rememberStations) {
        if (rememberStations) {
            if (startStation == null) {
                startStation = lastStartStation
                lastStartStation?.let { routeViewModel.onStartStationQueryChange(routeViewModel.getStationName(it)) }
            }
            if (endStation == null) {
                endStation = lastEndStation
                lastEndStation?.let { routeViewModel.onEndStationQueryChange(routeViewModel.getStationName(it)) }
            }
        } else {
            if (startStation == null) {
                startStation = favoriteStartStation
                favoriteStartStation?.let { routeViewModel.onStartStationQueryChange(routeViewModel.getStationName(it)) }
            }
            if (endStation == null) {
                endStation = favoriteEndStation
                favoriteEndStation?.let { routeViewModel.onEndStationQueryChange(routeViewModel.getStationName(it)) }
            }
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StationAutocompleteField(
                label = "Start Station",
                query = routeViewModel.startStationQuery.collectAsState().value,
                onQueryChange = { routeViewModel.onStartStationQueryChange(it) },
                suggestions = routeViewModel.startStationSuggestions.collectAsState().value,
                onStationSelected = { startStation = it },
                getStationName = { routeViewModel.getStationName(it) },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorite Start Station",
                    tint = if (startStation != null && startStation == favoriteStartStation) Color.Red else Color.Gray,
                    modifier = Modifier.pointerInput(startStation) {
                        detectTapGestures(
                            onLongPress = { 
                                startStation?.let { routeViewModel.toggleFavoriteStartStation(it) } 
                            }
                        )
                    }
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            StationAutocompleteField(
                label = "End Station",
                query = routeViewModel.endStationQuery.collectAsState().value,
                onQueryChange = { routeViewModel.onEndStationQueryChange(it) },
                suggestions = routeViewModel.endStationSuggestions.collectAsState().value,
                onStationSelected = { endStation = it },
                getStationName = { routeViewModel.getStationName(it) },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorite End Station",
                    tint = if (endStation != null && endStation == favoriteEndStation) Color.Red else Color.Gray,
                     modifier = Modifier.pointerInput(endStation) {
                        detectTapGestures(
                            onLongPress = { 
                                endStation?.let { routeViewModel.toggleFavoriteEndStation(it) }
                             }
                        )
                    }
                )
            }
        }

        Button(
            onClick = { 
                startStation?.let { start ->
                    endStation?.let { end ->
                        routeViewModel.findRoute(start, end) 
                    }
                }
            },
            enabled = startStation != null && endStation != null,
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ) {
            Text("Find Route")
        }

        when (val result = routeResult) {
            is RouteResult.Success -> {
                Text(text = "Route:", modifier = Modifier.padding(top = 16.dp), fontWeight = FontWeight.Bold)
                result.path.forEach { segment ->
                    Text(text = "Line ${segment.lineId}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    segment.stations.forEach { station ->
                        Text(text = routeViewModel.getStationName(station), modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }
            is RouteResult.Error -> {
                Text(text = result.message, modifier = Modifier.padding(top = 16.dp))
            }
            RouteResult.Empty -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationAutocompleteField(
    label: String,
    query: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<Station>,
    onStationSelected: (Station) -> Unit,
    getStationName: (Station) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = true
            },
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable).fillMaxWidth()
        )

        if (suggestions.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                suggestions.forEach { station ->
                    DropdownMenuItem(
                        text = { Text(getStationName(station)) },
                        onClick = {
                            onStationSelected(station)
                            onQueryChange(getStationName(station))
                            expanded = false
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    }
}