package pm.ear.railtrackhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pm.ear.railtrackhelper.data.UserPreferencesRepository
import pm.ear.railtrackhelper.ui.AppViewModelFactory
import pm.ear.railtrackhelper.ui.LinesScreen
import pm.ear.railtrackhelper.ui.PreferencesScreen
import pm.ear.railtrackhelper.ui.RoutePlannerScreen
import pm.ear.railtrackhelper.ui.RouteViewModel
import pm.ear.railtrackhelper.ui.theme.RailStationHelperTheme

class MainActivity : ComponentActivity() {

    private val userPreferencesRepository by lazy { UserPreferencesRepository(this) }
    private val routeViewModel by viewModels<RouteViewModel> {
        AppViewModelFactory(userPreferencesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RailStationHelperTheme {
                RailStationHelperApp(routeViewModel)
            }
        }
    }
}

@Composable
fun RailStationHelperApp(routeViewModel: RouteViewModel) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        routeViewModel.snackbarMessage.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { 
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) { 
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> {
                    RoutePlannerScreen(
                        routeViewModel = routeViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.LINES -> {
                    LinesScreen(
                        routeViewModel = routeViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.PREFERENCES -> {
                    PreferencesScreen(
                        routeViewModel = routeViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    LINES("Lines", Icons.Default.List),
    PREFERENCES("Preferences", Icons.Default.Settings)
}