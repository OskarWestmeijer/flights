package westmeijer.oskar.cache

import io.ktor.util.logging.*
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import westmeijer.oskar.models.FlightRoute


object RedisCache {

    private val log = KtorSimpleLogger("westmeijer.oskar.cache.RedisCache")

    private val client: RedisClient
    private val connection: StatefulRedisConnection<String, List<FlightRoute>>
    private val commands: RedisCommands<String, List<FlightRoute>>

    val flightRoutesKey = "routes:1"

    init {
        val redisUri = RedisURI.Builder.redis("localhost", 6379).build()
        client = RedisClient.create(redisUri)
        connection = client.connect(FlightRoutesCodec())
        commands = connection.sync()
        log.info("Established connection to Redis.")
    }

    fun set(key: String, value: List<FlightRoute>) {
        val result = commands.set(key, value)
        log.info("Set cache operation. key: {}, result: {}", key, result)
    }

    fun get(key: String): List<FlightRoute> {
        return commands.get(key)
    }

}