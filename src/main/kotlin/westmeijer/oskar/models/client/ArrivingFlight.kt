package westmeijer.oskar.models.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArrivingFlight(
    @SerialName("originAirport3LCode")
    val originAirport3LCode: String
)
