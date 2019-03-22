import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

data class OdsResponseDto(
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

class App {

    fun findCitiesContainingTrainStation() {
        val url = "https://data.sncf.com/api/records/1.0/search//?dataset=sncf-gares-et-arrets-transilien-ile-de-france&rows=1000"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        val body = client.newCall(request).execute().body()?.charStream()?.readText()
        val gson = Gson()
        val dto: OdsResponseDto = gson.fromJson(body, OdsResponseDto::class.java)
    }

    fun execute() {
        findCitiesContainingTrainStation()
    }
}

fun main(args: Array<String>) {
    App().execute()
}