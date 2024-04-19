package westmeijer.oskar.redis

import io.ktor.util.logging.*
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import westmeijer.oskar.models.server.FlightRoute


object Cache {

    private val log = KtorSimpleLogger("westmeijer.oskar.cache.RedisCache")

    private val cacheConnection: StatefulRedisConnection<String, List<FlightRoute>>
    private val cacheCommands: RedisCommands<String, List<FlightRoute>>
    const val FLIGHT_ROUTES_KEY = "routes:1"

    init {
        cacheConnection = RedisClient.client.connect(FlightRoutesCacheCodec())
        cacheCommands = cacheConnection.sync()
    }

    fun setCache(key: String, value: List<FlightRoute>) {
        val result = cacheCommands.set(key, value)
        log.info("Set cache operation. key: {}, result: {}", key, result)
    }

    fun getCache(key: String): List<FlightRoute> {
        return cacheCommands.get(key)
    }

}