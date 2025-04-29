package westmeijer.oskar.routes.model

import kotlinx.serialization.Serializable

@Serializable
data class UnmappedAirportResponse(
    val unmapped: Set<String>
)