package westmeijer.oskar.controller.model

import kotlinx.serialization.Serializable
import westmeijer.oskar.service.connections.model.Connection

@Serializable
data class ConnectionsResponse(
    val connections: List<Connection>,
    val importedAt: String
)
