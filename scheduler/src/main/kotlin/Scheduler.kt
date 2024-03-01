package westmeijer.oskar

import com.typesafe.config.ConfigFactory
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

    val shutdownSignal = CompletableDeferred<Unit>()

    init {
        val config = ConfigFactory.parseResources("scheduler.conf").resolve()

        // Access configuration properties
        val redisUrl = config.getString("redis")
        log.info("Redis url: $redisUrl")

        val redisUri = RedisURI.Builder.redis(redisUrl).build()
        client = RedisClient.create(redisUri)
        redisCommands = client.connect().sync()

        log.info("Launching scheduler with SADD.")
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            try {
                while (!shutdownSignal.isCompleted) {
                    log.info("Adding to set: $SCHEDULER_SET, msg: $EXPECTED_MSG")
                    redisCommands.sadd(SCHEDULER_SET, EXPECTED_MSG)
                    delay(Duration.of(10, ChronoUnit.MINUTES).toMillis())
                }
            } catch (e: Exception) {
                log.error("Error while scheduling: ${e.message}")
            }

        }

        Runtime.getRuntime().addShutdownHook(Thread {
            shutdownSignal.complete(Unit)
            scope.coroutineContext.cancel()
            client.shutdown()
        })
    }

}
