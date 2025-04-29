package westmeijer.oskar.routes.model

import kotlinx.serialization.Serializable
import westmeijer.oskar.services.connections.model.Connection

@Serializable
data class ConnectionsResponse(
    val connections: List<Connection>,
    val importedAt: String
)
