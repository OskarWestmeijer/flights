package westmeijer.oskar.services.airport.model

import kotlinx.serialization.Serializable

@Serializable
data class Airport(
    val airportCode: String,
    val airportName: String,
    val countryCode: String,
    val latitude: String,
    val longitude: String
) {

    init {
        // TODO: add validation tests
        require(airportCode.isNotBlank()) { "airportCode must not be blank" }
        require(airportName.isNotBlank()) { "airportName must not be blank" }
        require(countryCode.isNotBlank()) { "countryCode must not be blank" }
        require(latitude.isNotBlank()) { "latitude must not be blank" }
        require(longitude.isNotBlank()) { "longitude must not be blank" }
    }

}
