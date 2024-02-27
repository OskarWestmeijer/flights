package westmeijer.oskar.redis

import io.ktor.util.logging.*
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands
import westmeijer.oskar.models.FlightRoute


object Redis {

    private val log = KtorSimpleLogger("westmeijer.oskar.cache.RedisCache")

    private val client: RedisClient

    // cache
    private val cacheConnection: StatefulRedisConnection<String, List<FlightRoute>>
    private val cacheCommands: RedisCommands<String, List<FlightRoute>>
    const val FLIGHT_ROUTES_KEY = "routes:1"

    // pubsub
    private val pubSubConnection: StatefulRedisPubSubConnection<String, String>
    private val pubsubCommands: RedisPubSubCommands<String, String>
    const val SCHEDULER_CHANNEL = "refresh_routes"
    const val EXPECTED_MSG = "Refresh them routes."

    init {
        val redisUri = RedisURI.Builder.redis("localhost", 6379).build()
        client = RedisClient.create(redisUri)

        // cache
        cacheConnection = client.connect(FlightRoutesCodec())
        cacheCommands = cacheConnection.sync()

        // pubsub
        pubSubConnection = client.connectPubSub()
        pubSubConnection.addListener(SchedulerPubSubListener())
        pubsubCommands = pubSubConnection.sync()
        pubsubCommands.subscribe(SCHEDULER_CHANNEL)

        log.info("Established cache and pubsub connection to Redis.")
    }

    fun setCache(key: String, value: List<FlightRoute>) {
        val result = cacheCommands.set(key, value)
        log.info("Set cache operation. key: {}, result: {}", key, result)
    }

    fun getCache(key: String): List<FlightRoute> {
        return cacheCommands.get(key)
    }

}