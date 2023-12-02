package westmeijer.oskar.models

import kotlinx.serialization.Serializable

@Serializable
data class Airport(
    val airportCode: String,
    val latitude: String,
    val longitude: String
)
