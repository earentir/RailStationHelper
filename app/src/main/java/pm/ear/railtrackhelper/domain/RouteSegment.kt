package pm.ear.railtrackhelper.domain

import pm.ear.railtrackhelper.data.Station

data class RouteSegment(
    val lineId: String,
    val stations: List<Station>
)
