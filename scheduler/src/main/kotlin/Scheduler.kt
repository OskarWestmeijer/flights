package westmeijer.oskar

import com.typesafe.config.ConfigFactory
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

    var pool: JedisPool

    private val jedis: Jedis
    const val SCHEDULER_SET = "refresh_routes"
    const val EXPECTED_MSG = "Refresh them routes."

    init {
        val config = ConfigFactory.parseResources("scheduler.conf").resolve()

        // Access configuration properties
        val redisUrl = config.getString("redis")
        log.info("Redis url: $redisUrl")

        val poolConfig = JedisPoolConfig()
        pool = JedisPool(poolConfig, "redis://$redisUrl:6379")
        jedis = pool.resource
    }

    fun startSchedulerLoop() {
        log.info("Starting scheduler loop.")
        while (true) {
            try {
                log.info("Adding to set: $SCHEDULER_SET, msg: $EXPECTED_MSG")
                jedis.sadd(SCHEDULER_SET, EXPECTED_MSG)
                Thread.sleep(Duration.of(10, ChronoUnit.MINUTES).toMillis())
            } catch (e: Exception) {
                // jedis is not able to recover a lost connection to redis. therefore restart the service
                log.error("Error while scheduling. Shutdown after 10 seconds.", e)
                Thread.sleep(Duration.of(10, ChronoUnit.SECONDS).toMillis())
                exitProcess(0)
            }
        }
    }
}
