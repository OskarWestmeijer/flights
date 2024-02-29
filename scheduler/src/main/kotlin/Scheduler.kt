package westmeijer.oskar

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.temporal.ChronoUnit

object Scheduler {

    private val log: Logger = LoggerFactory.getLogger(Scheduler.javaClass)

    val client: RedisClient

    private val redisCommands: RedisCommands<String, String>
    const val SCHEDULER_SET = "refresh_routes"
    const val EXPECTED_MSG = "Refresh them routes."

    private val shutdownSignal = CompletableDeferred<Unit>()

    init {
        val redisUri = RedisURI.Builder.redis("localhost").build()
        client = RedisClient.create(redisUri)
        redisCommands = client.connect().sync()

        log.info("Launching scheduler with SADD.")
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            while (!shutdownSignal.isCompleted) {
                log.info("Adding to set: $SCHEDULER_SET, msg: $EXPECTED_MSG")
                redisCommands.sadd(SCHEDULER_SET, EXPECTED_MSG)
                delay(Duration.of(20, ChronoUnit.SECONDS).toMillis())
            }
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            shutdownSignal.complete(Unit)
            scope.coroutineContext.cancel()
            client.shutdown()
        })
    }

}
