import java.util.concurrent.TimeUnit

data class KnLocation(val lat: Double?, val lon: Double?, val name: String?)

data class KnStation(
    val name: String?,
    val cityName: String?,
    val uicCode: String?,
    val location: KnLocation?,
    val herCommuteMs: Long? = null,
    val hisCommuteMs: Long? = null
) {
    val herCommuteStr get() = format(herCommuteMs, "\uD83D\uDEBA")
    val hisCommuteStr get() = format(hisCommuteMs, "\uD83D\uDEB9")

    private fun format(timeMs: Long?, prefix: String): String {
        return timeMs?.let {
            "$prefix ${TimeUnit.MILLISECONDS.toMinutes(it)} minutes"
        } ?: "Unknown"
    }
}

