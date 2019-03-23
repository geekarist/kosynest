import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

data class SnResponse(
        val nhits: Int?,
        val parameters: Parameters?,
        val records: List<Record?>?
)

data class Record(
        val datasetid: String?,
        val fields: Fields?,
        val geometry: Geometry?,
        val record_timestamp: String?,
        val recordid: String?
)

data class Geometry(
        val coordinates: List<Double?>?,
        val type: String?
)

data class Fields(
        val adresse: String?,
        val code_insee_commune: String?,
        val code_uic: String?,
        val commune: String?,
        val coord_gps_wgs84: List<Double?>?,
        val gare_non_sncf: Double?,
        val libelle: String?,
        val libelle_point_d_arret: String?,
        val libelle_sms_gare: String?,
        val libelle_stif_info_voyageurs: String?,
        val nom_gare: String?,
        val uic7: String?,
        val x_lambert_ii_etendu: Double?,
        val y_lambert_ii_etendu: Double?,
        val zone_navigo: Double?
)

data class Parameters(
        val dataset: List<String?>?,
        val format: String?,
        val rows: Int?,
        val timezone: String?
)

class App(private val gson: Gson) {

    private fun findCitiesContainingTrainStation(): List<KnCity> {
        val body = fetchStationsDataset()
        return gson.fromJson(body, SnResponse::class.java).records
                ?.map { KnCity(it?.fields?.commune) }
                ?.sortedBy { it.name }
                ?.distinctBy { it.name }
                ?.filter { it.name?.isNotEmpty() == true }
                ?: listOf()
    }

    private fun fetchStationsDataset(): String? = File("sncf-gares-et-arrets-transilien-ile-de-france.json")
            .let { file ->
                if (file.exists()) {
                    println("Load cached dataset: ${file.toPath().fileName}")
                    file.readText()
                } else {
                    val url = "https://data.sncf.com/api/records/1.0/search//?dataset=sncf-gares-et-arrets-transilien-ile-de-france&rows=10000"
                    println("Fetch dataset from web service: ${url}")
                    val request = Request.Builder().url(url).build()
                    val client = OkHttpClient()
                    val json = client.newCall(request).execute().body()?.charStream()?.readText()
                            ?: throw Error("Remote dataset is null")
                    file.writeText(json)
                    json
                }
            }

    fun execute() {
        val cities = findCitiesContainingTrainStation()
        println("${cities.size} cities found:")
        println(cities.joinToString("\n") { "- ${it.name}" })
    }
}

data class KnCity(val name: String?)

fun main(args: Array<String>) {
    App(GsonBuilder().setPrettyPrinting().create()).execute()
}