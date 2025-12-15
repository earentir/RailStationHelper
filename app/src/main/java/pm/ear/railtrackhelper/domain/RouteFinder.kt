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

            val neighbors = getNeighbors(city, currentStation)

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

    private fun getNeighbors(city: City, station: Station): List<Station> {
        val neighbors = mutableSetOf<Station>()
        val allLinesInCity = city.lines

        // Find all occurrences of the station across all lines
        val stationOccurrences = allLinesInCity.flatMap { line ->
            line.stations.filter { it.nameEn == station.nameEn }.map { stationOnLine -> line to stationOnLine }
        }

        stationOccurrences.forEach { (line, stationOnLine) ->
            val stationIndex = line.stations.indexOfFirst { it.nameEn == stationOnLine.nameEn }

            // Add previous and next stations on the same line
            if (stationIndex > 0) {
                neighbors.add(line.stations[stationIndex - 1])
            }
            if (stationIndex < line.stations.size - 1) {
                neighbors.add(line.stations[stationIndex + 1])
            }

            // Add connections to other lines
            stationOnLine.connections.forEach { connectionLineId ->
                val connectedLine = allLinesInCity.find { it.lineId == connectionLineId }
                connectedLine?.stations?.find { it.nameEn == station.nameEn }?.let {
                    // This isn't quite right, a connection implies the ability to get on the other line.
                    // The logic should probably consider the other line's stations from this point.
                    // For now, we add the direct connections, and the BFS will explore from there.
                }
            }
        }
        
        return neighbors.toList().distinctBy { it.nameEn }
    }

    private fun segmentRoute(city: City, path: List<Station>): List<RouteSegment> {
        if (path.isEmpty()) return emptyList()

        val segments = mutableListOf<RouteSegment>()
        if (path.size == 1) {
            findLineForStation(city, path.first())?.let { lineId ->
                segments.add(RouteSegment(lineId, path))
            }
            return segments
        }

        var currentSegmentStations = mutableListOf(path.first())
        var currentLineId = findCommonLine(city, path[0], path[1])

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
        // Find any line the station is on
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