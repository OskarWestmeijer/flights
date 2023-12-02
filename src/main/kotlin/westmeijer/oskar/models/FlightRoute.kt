package westmeijer.oskar.models

import kotlinx.serialization.Serializable

@Serializable
data class FlightRoute(
    val id: Int,
    val from: Airport,
    val to: Airport
)
