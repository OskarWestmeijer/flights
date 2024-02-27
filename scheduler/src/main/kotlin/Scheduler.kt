package westmeijer.oskar

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.temporal.ChronoUnit

object Scheduler {

    val client: RedisClient

    private val pubSubConnection: StatefulRedisPubSubConnection<String, String>
    private val pubsubCommands: RedisPubSubCommands<String, String>
    const val SCHEDULER_CHANNEL = "refresh_routes"
    const val EXPECTED_MSG = "Refresh them routes."

    private val shutdownSignal = CompletableDeferred<Unit>()

    init {
        val redisUri = RedisURI.Builder.redis("localhost").build()
        client = RedisClient.create(redisUri)
        pubSubConnection = client.connectPubSub()
        pubsubCommands = pubSubConnection.sync()

        println("Launching pubsub scheduler.")
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            while (!shutdownSignal.isCompleted) {
                delay(Duration.of(20, ChronoUnit.SECONDS).toMillis())
                println("Publishing to: $SCHEDULER_CHANNEL, msg: $EXPECTED_MSG")
                pubsubCommands.publish(SCHEDULER_CHANNEL, EXPECTED_MSG)
            }
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            shutdownSignal.complete(Unit)
            scope.coroutineContext.cancel()
            pubSubConnection.close()
            client.shutdown()
        })
    }

}