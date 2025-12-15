package pm.ear.railtrackhelper.data

import android.content.Context
import com.google.gson.Gson

class MetroRepository(private val context: Context) {

    fun getCities(): List<City> {
        val cityFiles = listOf("athens.json", "thessaloniki.json")
        return cityFiles.mapNotNull {
            try {
                val jsonString = context.assets.open(it).bufferedReader().use { it.readText() }
                Gson().fromJson(jsonString, City::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}