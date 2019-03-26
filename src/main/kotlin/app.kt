import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

class App(private val gson: Gson) {

    private fun findTrainStations(): List<KnStation> {
        val body = fetchStationsDataset()
        return gson.fromJson(body, SnResponse::class.java).records
            ?.map { record ->
                KnStation(
                    name = record?.fields?.nom_gare,
                    cityName = record?.fields?.commune,
                    uicCode = record?.fields?.code_uic,
                    location = KnLocation(
                        record?.fields?.coord_gps_wgs84?.getOrNull(0),
                        record?.fields?.coord_gps_wgs84?.getOrNull(1),
                        record?.fields?.nom_gare
                    )
                )
            }
            ?.distinctBy { it.uicCode }
            ?.filter { it.cityName?.isNotEmpty() == true }
            ?.sortedBy { it.cityName }
            ?.filter { isNotRer(it) }
            ?.map {
                it.copy(
                    herCommuteMs = carItineraryDurationMs(it.location, LOCATION_HER_WORK),
                    hisCommuteMs = transitItineraryDurationMs(it.location, LOCATION_HIS_WORK)
                )
            }
            ?: listOf()
    }

    /**
     * Duration in transit between 2 locations.
     * @return the duration in milliseconds
     */
    private fun transitItineraryDurationMs(from: KnLocation?, to: KnLocation): Long {
        return TimeUnit.MINUTES.toMillis(51)
    }

    /**
     * Duration in car between 2 locations.
     * @return the duration in milliseconds
     */
    private fun carItineraryDurationMs(from: KnLocation?, to: KnLocation): Long {
        return TimeUnit.MINUTES.toMillis(42)
    }

    private fun isNotRer(it: KnStation): Boolean {
        val url =
            "https://data.sncf.com/api/records/1.0/search//?dataset=sncf-lignes-par-gares-idf&refine.code_uic=${it.uicCode}"
        val responseJson = fetch(url)
        val responseObj = gson.fromJson(responseJson, SnResponse::class.java)
        val fields = responseObj.records?.getOrNull(0)?.fields
        return fields?.train == 1 && fields.rer != 1
    }

    private fun fetch(url: String): String {
        File("cache").mkdirs()
        return File("cache/${url.hashCode()}.json").let { cachedFile ->
            if (cachedFile.exists()) {
                println("Load cached dataset: ${cachedFile.toPath().fileName}")
                cachedFile.readText()
            } else {
                println("Fetch dataset from web service: $url")
                val request = Request.Builder().url(url).build()
                val client = OkHttpClient()
                val json = client.newCall(request).execute().body()?.charStream()?.readText()
                    ?: throw Error("Remote dataset is null")
                cachedFile.writeText(json)
                json
            }
        }
    }

    private fun fetchStationsDataset(): String? {
        val url =
            "https://data.sncf.com/api/records/1.0/search//?dataset=sncf-gares-et-arrets-transilien-ile-de-france&rows=10000"
        return fetch(url)
    }

    fun execute() {
        // DONE: Display UIC of Transilien stations that are not RER
        // DONE: Display name of each station
        // DONE: Display city of each station
        // TODO: Display the duration of commute 1 from the station to evtech
        // TODO: Display the duration of commute 2 from the station to Gustave Roussy
        // TODO: Filter commute 1 < 60 min
        // TODO: Filter commute 2 < 60 min
        // TODO: Display station cityName, city, commute 1, commute 2
        val stations = findTrainStations()
        println("${stations.size} stations found:")
        println(stations.joinToString("\n") {
            "- ${it.cityName}, ${it.name}, ${it.uicCode}, ${it.herCommuteStr}, ${it.hisCommuteStr}"
        })
    }

    companion object {
        private val LOCATION_HIS_WORK = KnLocation(lat = 48.893205, lon = 2.237082, name = "OUI")
        private val LOCATION_HER_WORK = KnLocation(lat = 48.79444, lon = 2.348062, name = "GR")
    }
}

fun main(args: Array<String>) {
    App(GsonBuilder().setPrettyPrinting().create()).execute()
}