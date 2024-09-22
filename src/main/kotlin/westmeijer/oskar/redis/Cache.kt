package westmeijer.oskar.redis

import io.ktor.util.logging.*
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import westmeijer.oskar.models.server.Connection


object Cache {

    private val log = KtorSimpleLogger("westmeijer.oskar.cache.RedisCache")

    private val cacheConnection: StatefulRedisConnection<String, List<Connection>>
    private val cacheCommands: RedisCommands<String, List<Connection>>
    const val CONNECTIONS_KEY = "connections:1"

    init {
        cacheConnection = RedisClient.client.connect(ConnectionsCacheCodec())
        cacheCommands = cacheConnection.sync()
    }

    fun setCache(key: String, value: List<Connection>) {
        val result = cacheCommands.set(key, value)
        log.info("Set cache operation. key: {}, result: {}", key, result)
    }

    fun getCache(key: String): List<Connection> {
        return cacheCommands.get(key)
    }

}