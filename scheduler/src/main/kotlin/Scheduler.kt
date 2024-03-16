package westmeijer.oskar

import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.system.exitProcess

object Scheduler {

    private val log: Logger = LoggerFactory.getLogger(Scheduler::class.java)

    val pool: JedisPool

    private val jedis: Jedis
    const val SCHEDULER_SET = "refresh_routes"
    const val EXPECTED_MSG = "Refresh them routes."

    val shutdownSignal = CompletableDeferred<Unit>()

    init {
        val config = ConfigFactory.parseResources("scheduler.conf").resolve()

        // Access configuration properties
        val redisUrl = config.getString("redis")
        log.info("Redis url: $redisUrl")

        val poolConfig = JedisPoolConfig()
        pool = JedisPool(poolConfig, "redis://$redisUrl:6379")
        jedis = pool.resource

        log.info("Launching scheduler with SADD.")
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            try {
                while (shutdownSignal.isActive) {
                    log.info("Adding to set: $SCHEDULER_SET, msg: $EXPECTED_MSG")
                    jedis.sadd(SCHEDULER_SET, EXPECTED_MSG)
                    delay(Duration.of(10, ChronoUnit.SECONDS).toMillis())
                }
            } catch (e: Exception) {
                log.error("Error while scheduling.", e)
            } finally {
                log.info("Shutdown application. isActive: ${shutdownSignal.isActive}")
                scope.cancel()
                exitProcess(0)
            }

        }

        Runtime.getRuntime().addShutdownHook(Thread {
            shutdownSignal.complete(Unit)
            scope.coroutineContext.cancel()
            pool.close()
        })
    }

}
