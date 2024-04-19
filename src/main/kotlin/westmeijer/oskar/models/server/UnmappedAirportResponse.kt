package westmeijer.oskar.models.server

import kotlinx.serialization.Serializable

@Serializable
data class UnmappedAirportResponse(
    val unmapped: Set<String>
)