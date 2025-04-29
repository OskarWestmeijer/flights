package westmeijer.oskar.services.connections

import io.ktor.util.logging.*
import westmeijer.oskar.redis.CacheService
import westmeijer.oskar.services.connections.model.Connection

object ConnectionsService {

    private val log = KtorSimpleLogger("westmeijer.oskar.services.connections.ConnectionsService")

    fun getConnections(): List<Connection> {
        return CacheService.getCache(CacheService.CONNECTIONS_KEY)
    }

}