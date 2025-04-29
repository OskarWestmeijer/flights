package westmeijer.oskar.controller.model

import kotlinx.serialization.Serializable

@Serializable
data class UnmappedAirportResponse(
    val unmapped: Set<String>
)