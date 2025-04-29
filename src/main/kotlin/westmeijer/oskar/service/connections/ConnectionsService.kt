package westmeijer.oskar.service.connections

import io.ktor.util.logging.*
import westmeijer.oskar.service.cache.CacheService
import westmeijer.oskar.service.connections.model.Connection

object ConnectionsService {

    private val log = KtorSimpleLogger("westmeijer.oskar.services.connections.ConnectionsService")

    fun getConnections(): List<Connection> {
        return CacheService.getCache(CacheService.CONNECTIONS_KEY)
    }

}