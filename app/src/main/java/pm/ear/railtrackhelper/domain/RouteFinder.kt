package pm.ear.railtrackhelper.domain

import pm.ear.railtrackhelper.data.City
import pm.ear.railtrackhelper.data.Station
import java.util.LinkedList

class RouteFinder {

    fun findRoute(city: City, startStation: Station, endStation: Station): List<RouteSegment>? {
        if (startStation == endStation) return emptyList()

        val queue = LinkedList<List<Station>>()
        val visited = mutableSetOf<Station>()

        queue.add(listOf(startStation))
        visited.add(startStation)

        while (queue.isNotEmpty()) {
            val currentPath = queue.poll()
            val currentStation = currentPath.last()

            if (currentStation == endStation) {
                return segmentRoute(currentPath)
            }

            val neighbors = getNeighbors(city, currentStation)

            for (neighbor in neighbors) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    val newPath = currentPath.toMutableList()
                    newPath.add(neighbor)
                    queue.add(newPath)
                }
            }
        }
        return null // No path found
    }

    private fun getNeighbors(city: City, station: Station): List<Station> {
        val neighbors = mutableListOf<Station>()
        station.lines.forEach { lineId ->
            val line = city.lines.find { it.lineId == lineId }
            line?.stations?.let { stationNames ->
                val stationIndex = stationNames.indexOf(station.nameEn)
                if (stationIndex > 0) {
                    city.stations.find { it.nameEn == stationNames[stationIndex - 1] }?.let { neighbors.add(it) }
                }
                if (stationIndex < stationNames.size - 1) {
                    city.stations.find { it.nameEn == stationNames[stationIndex + 1] }?.let { neighbors.add(it) }
                }
            }
        }
        return neighbors.distinct()
    }

    private fun segmentRoute(path: List<Station>): List<RouteSegment> {
        if (path.isEmpty()) return emptyList()

        val segments = mutableListOf<RouteSegment>()
        var currentSegmentStations = mutableListOf(path.first())
        var currentLine = findCommonLine(path[0], path.getOrNull(1))

        for (i in 1 until path.size) {
            val previousStation = path[i-1]
            val currentStation = path[i]
            val commonLine = findCommonLine(previousStation, currentStation)

            if (commonLine != currentLine) {
                currentLine?.let { segments.add(RouteSegment(it, currentSegmentStations)) }
                currentSegmentStations = mutableListOf(previousStation, currentStation)
                currentLine = commonLine
            } else {
                currentSegmentStations.add(currentStation)
            }
        }
        currentLine?.let { segments.add(RouteSegment(it, currentSegmentStations.distinct())) }
        return segments
    }

    private fun findCommonLine(station1: Station, station2: Station?): String? {
        station2 ?: return station1.lines.firstOrNull()
        return station1.lines.intersect(station2.lines.toSet()).firstOrNull()
    }
}