import okhttp3.OkHttpClient
import okhttp3.Request

fun main(args: Array<String>) {
    val orig = "48.893205,2.237082"
    val dest = "48.79444,2.348062"
    val key = "AIzaSyDgj6Fm2RTlGBDXlSSjGMxpQD4tZTvM8nU"
    val host = "maps.googleapis.com"
    val path = "maps/api/directions/json"
    val url = "https://$host/$path?origin=$orig&destination=$dest&key=$key"

    val request = Request.Builder().url(url).build()
    val client = OkHttpClient()
    val json = client.newCall(request).execute().body()?.charStream()?.readText()
        ?: throw Error("Remote dataset is null")

    println(json)
    TODO("Deserialize")
}