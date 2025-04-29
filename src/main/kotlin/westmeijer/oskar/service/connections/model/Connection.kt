package westmeijer.oskar.service.connections.model

import kotlinx.serialization.Serializable
import westmeijer.oskar.service.airport.model.Airport

@Serializable
data class Connection(
    val hamAirport: Airport,
    val connectionAirport: Airport,
    val departureFlightCount: Int,
    val arrivalFlightCount: Int,
    val totalFlightCount: Int,
    val importedAt: String
)
