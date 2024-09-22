package westmeijer.oskar.models.server

import kotlinx.serialization.Serializable

@Serializable
data class Connection(
    val hamAirport: Airport,
    val connectionAirport: Airport,
    val departureFlightCount: Int,
    val arrivalFlightCount: Int,
    val totalFlightCount: Int,
    val importedAt: String
)
