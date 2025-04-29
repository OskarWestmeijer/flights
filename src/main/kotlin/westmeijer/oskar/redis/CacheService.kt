package westmeijer.oskar.redis

import io.ktor.util.logging.*
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import westmeijer.oskar.services.connections.model.Connection


object CacheService {

    private val log = KtorSimpleLogger("westmeijer.oskar.cache.RedisCache")

    private val cacheConnection: StatefulRedisConnection<String, List<Connection>> = RedisClient.client.connect(ConnectionsCacheCodec())
    private val cacheCommands: RedisCommands<String, List<Connection>> = cacheConnection.sync()
    const val CONNECTIONS_KEY = "connections:1"

    fun setCache(key: String, value: List<Connection>) {
        val result = cacheCommands.set(key, value)
        log.info("Set cache operation. key: {}, result: {}", key, result)
    }

    fun getCache(key: String): List<Connection> {
        return cacheCommands.get(key)
    }

}