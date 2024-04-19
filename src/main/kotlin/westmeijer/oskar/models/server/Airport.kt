package westmeijer.oskar.models.server

import kotlinx.serialization.Serializable

@Serializable
data class Airport(
    val airportCode: String,
    val airportName: String,
    val countryCode: String,
    val latitude: String,
    val longitude: String
)
