package westmeijer.oskar.service.importer.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArrivingFlight(
    @SerialName("originAirport3LCode")
    val originAirport3LCode: String,

    @SerialName("flightnumber")
    val flightNumber: String,

    @SerialName("airlineName")
    val airlineName: String,

    @SerialName("plannedArrivalTime")
    val plannedArrivalTime: String
)
