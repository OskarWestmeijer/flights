package westmeijer.oskar.models.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DepartingFlight(
    @SerialName("destinationAirport3LCode")
    val destinationAirport3LCode: String
)