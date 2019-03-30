import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileReader
import java.util.*
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
            //?.filter { it.cityName?.startsWith("E") == true }
            ?.also { println("${it.size} stations found") }
            ?.asSequence()
            ?.distinctBy { it.uicCode }
            ?.filter { it.cityName?.isNotEmpty() == true }
            ?.sortedBy { it.cityName }
            ?.filter { isNotRer(it) }
            ?.map { knStation ->
                val herCommuteMs = knStation.location?.let {
                    carItineraryDurationMs(it, LOCATION_HER_WORK)
                }
                val hisCommuteMs = knStation.location?.let {
                    transitItineraryDurationMs(it, LOCATION_HIS_WORK)
                }
                knStation.copy(herCommuteMs = herCommuteMs, hisCommuteMs = hisCommuteMs)
            }?.filter {
                it.hisCommuteMs != null
                        && it.herCommuteMs != null
                        && it.hisCommuteMs < TimeUnit.MINUTES.toMillis(90)
                        && it.herCommuteMs < TimeUnit.MINUTES.toMillis(90)
            }
            ?.toList()
            ?: listOf()
    }

    /**
     * Duration in transit between 2 locations.
     * @return the duration in milliseconds
     */
    private fun transitItineraryDurationMs(from: KnLocation, to: KnLocation): Long? {
        val baseUrl = "https://api.sncf.com/v1/coverage/sncf/journeys"
        val fromValue = "${from.lon};${from.lat}"
        val toValue = "${to.lon};${to.lat}"
        val dateTimeValue = "20190319T090000"
        val url = "$baseUrl?from=$fromValue&to=$toValue&datetime=$dateTimeValue"
        val keyClear = configuration["navitia.api.key"]
        val keyBase64 = Base64.getEncoder().encodeToString("$keyClear:".toByteArray())
        val headers = Headers.of(mapOf("Authorization" to "Basic $keyBase64"))
        val json = fetch(url, headers)
        val response = gson.fromJson(json, NavitiaResponse::class.java)
        val durationSec = response.journeys?.get(0)?.duration?.toLong()
        return durationSec?.let { TimeUnit.SECONDS.toMillis(it) }
    }

    private val configuration by lazy { Properties().apply { load(FileReader("private/conf.properties")) } }

    /**
     * Duration in car between 2 locations.
     * @return the duration in milliseconds
     */
    private fun carItineraryDurationMs(from: KnLocation, to: KnLocation): Long? {

        val orig = "${from.lat},${from.lon}"
        val dest = "${to.lat},${to.lon}"
        val mode = "driving"
        val key = configuration["google.api.key"]
        val host = "maps.googleapis.com"
        val path = "maps/api/directions/json"
        val url = "https://$host/$path?origin=$orig&destination=$dest&key=$key&mode=$mode"

        val json = fetch(url)
        val deserialized = gson.fromJson(json, GdResponse::class.java)

        val durationSec = deserialized?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.duration?.value?.toLong()
        return durationSec?.let { TimeUnit.SECONDS.toMillis(it) }
    }

    private fun isNotRer(it: KnStation): Boolean {
        val url =
            "https://data.sncf.com/api/records/1.0/search//?dataset=sncf-lignes-par-gares-idf&refine.code_uic=${it.uicCode}"
        val responseJson = fetch(url)
        val responseObj = gson.fromJson(responseJson, SnResponse::class.java)
        val fields = responseObj.records?.getOrNull(0)?.fields
        return fields?.train == 1 && fields.rer != 1
    }

    private fun fetch(url: String, headers: Headers = Headers.of()): String {
        File("cache").mkdirs()
        return File("cache/${url.hashCode()}.json").let { cachedFile ->
            if (cachedFile.exists()) {
                println("Load cached dataset: ${cachedFile.toPath().fileName}")
                cachedFile.readText()
            } else {
                println("Fetch dataset from web service: $url")
                val request = Request.Builder()
                    .url(url)
                    .headers(headers)
                    .build()
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
        // DONE: Display the duration of commute from the station to Gustave Roussy
        // DONE: Display the duration of commute from the station to evtech
        // DONE: Filter commute 1 < 60 min
        // DONE: Filter commute 2 < 60 min
        // DONE: Display station cityName, city, commute 1, commute 2
        val stations = findTrainStations()
        println()
        println("${stations.size} matching stations:")
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