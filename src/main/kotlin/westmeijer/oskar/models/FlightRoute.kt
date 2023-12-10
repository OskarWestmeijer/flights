package westmeijer.oskar.models

import kotlinx.serialization.Serializable

@Serializable
data class FlightRoute(
    val hamAirport: Airport,
    val connectionAirport: Airport,
    val flightCount: Int
)
