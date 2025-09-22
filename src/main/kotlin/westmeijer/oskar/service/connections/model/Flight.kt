package westmeijer.oskar.service.connections.model

import kotlinx.serialization.Serializable

@Serializable
data class Flight(
    val flightType: FlightType,
    val flightNumber: String,
    val airlineName: String,
    val plannedTime: String
)

enum class FlightType {
    ARRIVAL_HAM,
    DEPARTURE_HAM
}
