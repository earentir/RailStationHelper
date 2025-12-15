package pm.ear.railtrackhelper.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pm.ear.railtrackhelper.data.MetroLine
import pm.ear.railtrackhelper.data.Station

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinesScreen(modifier: Modifier = Modifier, routeViewModel: RouteViewModel = viewModel()) {
    val selectedCity by routeViewModel.selectedCity.collectAsState()
    var selectedLine by remember { mutableStateOf<MetroLine?>(null) }
    var lineExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = lineExpanded,
            onExpandedChange = { lineExpanded = !lineExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedLine?.lineId ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select a line") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = lineExpanded)
                },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = lineExpanded, onDismissRequest = { lineExpanded = false }) {
                selectedCity.lines.forEach { line ->
                    DropdownMenuItem(
                        text = { Text(line.lineId) },
                        onClick = {
                            selectedLine = line
                            lineExpanded = false
                        }
                    )
                }
            }
        }

        selectedLine?.let { line ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Stations for line ${line.lineId}:")
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(line.stations) { index, stationName ->
                    val station = selectedCity.stations.find { it.nameEn == stationName }!!
                    StationTimeline(station, line.lineId, routeViewModel, index == 0, index == line.stations.size - 1)
                }
            }
        }
    }
}

@Composable
fun StationTimeline(station: Station, lineId: String, routeViewModel: RouteViewModel, isFirst: Boolean, isLast: Boolean) {
    val lineColor = getLineColor(lineId)
    val isConnection = station.lines.size > 1

    Row(modifier = Modifier.height(64.dp), verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.width(48.dp).fillMaxHeight()) {
            val strokeWidth = 8f
            val xCenter = size.width / 2
            val yCenter = size.height / 2
            val startY = if (isFirst) yCenter else 0f
            val endY = if (isLast) yCenter else size.height

            // Draw the line
            drawLine(
                color = lineColor,
                start = Offset(x = xCenter, y = startY),
                end = Offset(x = xCenter, y = endY),
                strokeWidth = strokeWidth
            )

            if (isConnection) {
                // Hollow circle for connection
                drawCircle(
                    color = Color.White,
                    radius = 18f,
                    center = Offset(x = xCenter, y = yCenter)
                )
                drawCircle(
                    color = lineColor,
                    radius = 18f,
                    center = Offset(x = xCenter, y = yCenter),
                    style = Stroke(width = strokeWidth)
                )
            } else {
                // Solid circle for regular stop
                drawCircle(
                    color = lineColor,
                    radius = 12f,
                    center = Offset(x = xCenter, y = yCenter)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(routeViewModel.getStationName(station))

        if (isConnection) {
            Spacer(Modifier.width(8.dp))
            Text("(${station.lines.joinToString()})")
        }
    }
}

fun getLineColor(lineId: String): Color {
    return when (lineId) {
        "M1" -> Color(0xFF009639)
        "M2" -> Color(0xFFDA2128)
        "M3" -> Color(0xFF0077C0)
        "T1" -> Color(0xFFD6000D) // Thessaloniki Line 1
        "T2" -> Color(0xFF005AAA) // Thessaloniki Line 2
        else -> Color.Gray
    }
}
