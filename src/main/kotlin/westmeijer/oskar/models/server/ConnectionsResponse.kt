package westmeijer.oskar.models.server

import kotlinx.serialization.Serializable

@Serializable
data class ConnectionsResponse(
    val connections: List<Connection>,
    val importedAt: String
)
