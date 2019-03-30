data class NavitiaResponse(
    val context: Context?,
    val disruptions: List<Any?>?,
    val exceptions: List<Any?>?,
    val feed_publishers: List<Any?>?,
    val journeys: List<Journey?>?,
    val links: List<Link?>?,
    val notes: List<Any?>?,
    val tickets: List<Any?>?
)

data class Journey(
    val arrival_date_time: String?,
    val calendars: List<Calendar?>?,
    val co2_emission: Co2Emission?,
    val departure_date_time: String?,
    val distances: Distances?,
    val duration: Int?,
    val durations: Durations?,
    val fare: Fare?,
    val links: List<Link?>?,
    val nb_transfers: Int?,
    val requested_date_time: String?,
    val sections: List<Section?>?,
    val status: String?,
    val tags: List<String?>?,
    val type: String?
)

data class Durations(
    val bike: Int?,
    val car: Int?,
    val ridesharing: Int?,
    val total: Int?,
    val walking: Int?
)

data class Fare(
    val found: Boolean?,
    val links: List<Any?>?,
    val total: Total?
)

data class Total(
    val value: String?
)

data class Link(
    val href: String?,
    val rel: String?,
    val templated: Boolean?,
    val type: String?
)

data class Distances(
    val bike: Int?,
    val car: Int?,
    val ridesharing: Int?,
    val walking: Int?
)

data class Calendar(
    val active_periods: List<ActivePeriod?>?,
    val week_pattern: WeekPattern?
)

data class WeekPattern(
    val friday: Boolean?,
    val monday: Boolean?,
    val saturday: Boolean?,
    val sunday: Boolean?,
    val thursday: Boolean?,
    val tuesday: Boolean?,
    val wednesday: Boolean?
)

data class ActivePeriod(
    val begin: String?,
    val end: String?
)

data class Co2Emission(
    val unit: String?,
    val value: Double?
)

data class Section(
    val additional_informations: List<String?>?,
    val arrival_date_time: String?,
    val base_arrival_date_time: String?,
    val base_departure_date_time: String?,
    val co2_emission: Co2Emission?,
    val data_freshness: String?,
    val departure_date_time: String?,
    val display_informations: DisplayInformations?,
    val duration: Int?,
    val from: From?,
    val geojson: Geojson?,
    val id: String?,
    val links: List<Any?>?,
    val mode: String?,
    val stop_date_times: List<StopDateTime?>?,
    val to: To?,
    val transfer_type: String?,
    val type: String?
)

data class To(
    val address: Address?,
    val embedded_type: String?,
    val id: String?,
    val name: String?,
    val quality: Int?
)

data class Address(
    val administrative_regions: List<AdministrativeRegion?>?,
    val coord: Coord?,
    val house_number: Int?,
    val id: String?,
    val label: String?,
    val name: String?
)

data class AdministrativeRegion(
    val coord: Coord?,
    val id: String?,
    val insee: String?,
    val label: String?,
    val level: Int?,
    val name: String?,
    val zip_code: String?
)

data class Coord(
    val lat: String?,
    val lon: String?
)

data class StopDateTime(
    val additional_informations: List<Any?>?,
    val arrival_date_time: String?,
    val base_arrival_date_time: String?,
    val base_departure_date_time: String?,
    val departure_date_time: String?,
    val links: List<Any?>?,
    val stop_point: StopPoint?
)

data class StopPoint(
    val coord: Coord?,
    val equipments: List<Any?>?,
    val fare_zone: FareZone?,
    val id: String?,
    val label: String?,
    val links: List<Any?>?,
    val name: String?
)

data class FareZone(
    val name: String?
)

data class DisplayInformations(
    val code: String?,
    val color: String?,
    val commercial_mode: String?,
    val description: String?,
    val direction: String?,
    val equipments: List<Any?>?,
    val headsign: String?,
    val label: String?,
    val links: List<Any?>?,
    val name: String?,
    val network: String?,
    val physical_mode: String?,
    val text_color: String?
)

data class From(
    val embedded_type: String?,
    val id: String?,
    val name: String?,
    val quality: Int?,
    val stop_point: StopPointX?
)

data class StopPointX(
    val administrative_regions: List<AdministrativeRegion?>?,
    val coord: Coord?,
    val equipments: List<Any?>?,
    val fare_zone: FareZone?,
    val id: String?,
    val label: String?,
    val links: List<Any?>?,
    val name: String?,
    val stop_area: StopArea?
)

data class StopArea(
    val codes: List<Code?>?,
    val coord: Coord?,
    val id: String?,
    val label: String?,
    val links: List<Any?>?,
    val name: String?,
    val timezone: String?
)

data class Code(
    val type: String?,
    val value: String?
)

data class Geojson(
    val coordinates: List<Any?>?,
    val properties: List<Property?>?,
    val type: String?
)

data class Property(
    val length: Int?
)

data class Context(
    val car_direct_path: CarDirectPath?,
    val current_datetime: String?,
    val timezone: String?
)

data class CarDirectPath(
    val co2_emission: Co2Emission?
)