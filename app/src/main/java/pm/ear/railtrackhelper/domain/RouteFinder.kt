package pm.ear.railtrackhelper.domain

import pm.ear.railtrackhelper.data.City
import pm.ear.railtrackhelper.data.Station
import java.util.LinkedList

class RouteFinder {

    fun findRoute(city: City, startStation: Station, endStation: Station): List<RouteSegment>? {
        if (startStation.nameEn == endStation.nameEn) return emptyList()

        val queue = LinkedList<List<Station>>()
        val visited = mutableSetOf<String>()
        val allStations = city.lines.flatMap { it.stations }.associateBy { it.nameEn }

        queue.add(listOf(startStation))
        visited.add(startStation.nameEn)

        while (queue.isNotEmpty()) {
            val currentPath = queue.poll()
            val currentStation = currentPath.last()

            if (currentStation.nameEn == endStation.nameEn) {
                return segmentRoute(city, currentPath)
            }

            val neighbors = getNeighbors(city, currentStation, allStations)

            for (neighbor in neighbors) {
                if (neighbor.nameEn !in visited) {
                    visited.add(neighbor.nameEn)
                    val newPath = currentPath.toMutableList()
                    newPath.add(neighbor)
                    queue.add(newPath)
                }
            }
        }
        return null // No path found
    }

    private fun getNeighbors(city: City, station: Station, allStations: Map<String, Station>): List<Station> {
        val neighbors = mutableSetOf<Station>()

        // Find neighbors on the same line(s)
        city.lines.forEach { line ->
            val stationIndex = line.stations.indexOfFirst { it.nameEn == station.nameEn }
            if (stationIndex != -1) {
                if (stationIndex > 0) {
                    neighbors.add(line.stations[stationIndex - 1])
                }
                if (stationIndex < line.stations.size - 1) {
                    neighbors.add(line.stations[stationIndex + 1])
                }
            }
        }

        // Find and add transfer stations from other lines
        station.connections.forEach { lineId ->
            city.lines.find { it.lineId == lineId }?.let { line ->
                line.stations.find { it.nameEn == station.nameEn }?.let {
                    // This is a station on another line with the same name, representing a transfer
                    // We need to add its neighbors as well.
                    val transferStationIndex = line.stations.indexOfFirst { it.nameEn == station.nameEn }
                    if (transferStationIndex > 0) {
                        neighbors.add(line.stations[transferStationIndex - 1])
                    }
                    if (transferStationIndex < line.stations.size - 1) {
                        neighbors.add(line.stations[transferStationIndex + 1])
                    }
                }
            }
        }
        return neighbors.toList()
    }

    private fun segmentRoute(city: City, path: List<Station>): List<RouteSegment> {
        if (path.isEmpty()) return emptyList()

        val segments = mutableListOf<RouteSegment>()
        var currentSegmentStations = mutableListOf(path.first())
        var currentLineId = findLineForStation(city, path.first())

        for (i in 1 until path.size) {
            val previousStation = path[i - 1]
            val currentStation = path[i]
            
            val commonLineId = findCommonLine(city, previousStation, currentStation)

            if (commonLineId != currentLineId) {
                currentLineId?.let { segments.add(RouteSegment(it, ArrayList(currentSegmentStations))) }
                currentSegmentStations = mutableListOf(previousStation, currentStation)
                currentLineId = findLineForStation(city, currentStation, previousStation)
            } else {
                currentSegmentStations.add(currentStation)
            }
        }
        currentLineId?.let { segments.add(RouteSegment(it, currentSegmentStations.distinctBy { it.nameEn })) }
        return segments
    }

    private fun findLineForStation(city: City, station: Station, previousStation: Station? = null): String? {
        if (previousStation != null) {
            return findCommonLine(city, station, previousStation)
        }
        return city.lines.find { line -> line.stations.any { it.nameEn == station.nameEn } }?.lineId
    }

    private fun findCommonLine(city: City, station1: Station, station2: Station): String? {
        return city.lines.find { line ->
            val station1Index = line.stations.indexOfFirst { it.nameEn == station1.nameEn }
            val station2Index = line.stations.indexOfFirst { it.nameEn == station2.nameEn }
            station1Index != -1 && station2Index != -1 && (station1Index - station2Index == 1 || station1Index - station2Index == -1)
        }?.lineId
    }
}