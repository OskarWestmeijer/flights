package westmeijer.oskar.service.connections.model

import kotlinx.serialization.Serializable
import westmeijer.oskar.service.airport.model.Airport

@Serializable
data class Flight(
    val flightType: FlightType,
    val flightNumber: String,
    val airlineName: String,
    val plannedTime: String,
    val connectionAirport: Airport
)

enum class FlightType {
    ARRIVAL_HAM,
    DEPARTURE_HAM
}
