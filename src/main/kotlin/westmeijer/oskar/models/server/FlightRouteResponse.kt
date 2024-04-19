package westmeijer.oskar.models.server

import kotlinx.serialization.Serializable

@Serializable
data class FlightRouteResponse(
    val flightRoutes: List<FlightRoute>,
    val importedAt: String
)
