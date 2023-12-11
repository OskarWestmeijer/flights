package westmeijer.oskar.models

import kotlinx.serialization.Serializable

@Serializable
data class UnmappedAirportResponse(
    val unmapped: Set<String>
)