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
                itemsIndexed(line.stations) { index, station ->
                    StationTimeline(station, line, routeViewModel, index == 0, index == line.stations.size - 1)
                }
            }
        }
    }
}

@Composable
fun StationTimeline(station: Station, line: MetroLine, routeViewModel: RouteViewModel, isFirst: Boolean, isLast: Boolean) {
    val lineColor = Color(android.graphics.Color.parseColor(line.lineColor))
    val isConnection = station.connections.isNotEmpty()

    Row(modifier = Modifier.height(64.dp), verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.width(120.dp).fillMaxHeight()) {
            val strokeWidth = 8f
            val xCenter = size.width / 3 // Shift the main line to the left
            val yCenter = size.height / 2
            val startY = if (isFirst) yCenter else 0f
            val endY = if (isLast) yCenter else size.height
            val circleRadius = 18f

            // Draw the main timeline
            drawLine(
                color = lineColor,
                start = Offset(x = xCenter, y = startY),
                end = Offset(x = xCenter, y = endY),
                strokeWidth = strokeWidth
            )

            if (isConnection) {
                // Main connection circle (hollow)
                drawCircle(color = Color.White, radius = circleRadius, center = Offset(x = xCenter, y = yCenter))
                drawCircle(color = lineColor, radius = circleRadius, center = Offset(x = xCenter, y = yCenter), style = Stroke(width = strokeWidth))

                // Draw connection lines and dots horizontally
                val connectionLineLength = circleRadius * 2
                val startX = xCenter + circleRadius
                val endX = startX + connectionLineLength

                // Gray line extending to the right
                drawLine(
                    color = Color.Gray,
                    start = Offset(x = startX, y = yCenter),
                    end = Offset(x = endX, y = yCenter),
                    strokeWidth = strokeWidth / 2
                )

                station.connections.forEachIndexed { index, connectionLineId ->
                    val connectionLine = routeViewModel.selectedCity.value.lines.find { it.lineId == connectionLineId }
                    connectionLine?.let {
                        val connectionColor = Color(android.graphics.Color.parseColor(it.lineColor))
                        val dotX = endX + (index * (circleRadius * 2 + 8.dp.toPx())) + circleRadius
                        
                        drawCircle(
                            color = connectionColor,
                            radius = circleRadius,
                            center = Offset(x = dotX, y = yCenter)
                        )
                    }
                }

            } else {
                // Regular station dot (solid)
                drawCircle(color = lineColor, radius = 12f, center = Offset(x = xCenter, y = yCenter))
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(routeViewModel.getStationName(station))

        if (isConnection) {
            Spacer(Modifier.width(8.dp))
            Text("(${station.connections.joinToString()})")
        }
    }
}
