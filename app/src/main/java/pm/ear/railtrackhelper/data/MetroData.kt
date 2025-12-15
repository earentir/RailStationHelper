package pm.ear.railtrackhelper.data

import android.content.Context

// Your superior, cleaner data structure.
data class Station(
    val nameEn: String,
    val nativeName: String, // The correct, generic key for the native language name.
    val connections: List<String>
)

data class MetroLine(
    val lineId: String,
    val lineType: String, // e.g., "Metro", "Proastiakos", "Tram"
    val lineColor: String, // Hex color string
    val stations: List<Station>
)

data class City(
    val name: String,
    val lines: List<MetroLine>
)

// The MetroData object now only handles initialization.
object MetroData {
    lateinit var cities: List<City>

    fun init(context: Context) {
        cities = MetroRepository(context).getCities()
    }
}