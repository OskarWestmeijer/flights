package westmeijer.oskar.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Destination(
    @SerialName("destinationAirport3LCode")
    val destinationAirport3LCode: String
)