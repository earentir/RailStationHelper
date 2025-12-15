package pm.ear.railtrackhelper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pm.ear.railtrackhelper.data.MetroData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(modifier: Modifier = Modifier, routeViewModel: RouteViewModel = viewModel()) {
    val selectedCity by routeViewModel.selectedCity.collectAsState()
    val displayLanguage by routeViewModel.displayLanguage.collectAsState()
    val rememberStations by routeViewModel.rememberStations.collectAsState()
    val utilizeAllRailTypes by routeViewModel.utilizeAllRailTypes.collectAsState()
    var cityExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }
    var showClearFavoritesDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = cityExpanded,
            onExpandedChange = { cityExpanded = !cityExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedCity.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("City") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded)
                },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = cityExpanded, onDismissRequest = { cityExpanded = false }) {
                MetroData.cities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city.name) },
                        onClick = {
                            routeViewModel.setSelectedCity(city.name)
                            cityExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = languageExpanded,
            onExpandedChange = { languageExpanded = !languageExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = displayLanguage,
                onValueChange = {},
                readOnly = true,
                label = { Text("Language") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded)
                },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = languageExpanded, onDismissRequest = { languageExpanded = false }) {
                listOf("English", "Native").forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language) },
                        onClick = {
                            routeViewModel.setDisplayLanguage(language)
                            languageExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Remember Last Stations")
            Switch(
                checked = rememberStations,
                onCheckedChange = { routeViewModel.setRememberStations(it) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Utilize All Rail Types")
            Switch(
                checked = utilizeAllRailTypes,
                onCheckedChange = { routeViewModel.setUtilizeAllRailTypes(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showClearFavoritesDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Clear All Favorites")
        }
    }

    if (showClearFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showClearFavoritesDialog = false },
            title = { Text("Clear Favorites") },
            text = { Text("Are you sure you want to clear all favorite stations for ${selectedCity.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        routeViewModel.clearAllFavoriteStations()
                        showClearFavoritesDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearFavoritesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}