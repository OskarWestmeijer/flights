import io.ktor.util.logging.*
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import westmeijer.oskar.redis.RedisClient
import westmeijer.oskar.services.FlightRoutesService

object SchedulerListener {

    private val log = KtorSimpleLogger("westmeijer.oskar.redis.SchedulerListener")

    private val redisCommands: RedisCommands<String, String>
    const val SCHEDULER_SET = "refresh_routes"
    const val EXPECTED_MSG = "Refresh them routes."

    init {
        redisCommands = RedisClient.client.connect().sync()
    }

    fun startListening(scope: CoroutineScope) {
        scope.launch {
            try {
                log.info("Starting scheduler loop.")
                while (isActive) {
                    try {
                        val message = redisCommands.spop(SCHEDULER_SET)
                        if (message != null) {
                            handleReceivedMessage(message)
                        } else {
                            delay(1000)
                        }
                    } catch (e: Exception) {
                        log.error("Error inside scheduler loop. isActive: $isActive", e)
                        delay(1000)
                    }
                }
            } catch (e: Exception) {
                log.error("Error outside scheduler loop.", e)
            } finally {
                log.info("Finally of scheduler listener coroutine.  isActive: $isActive")
            }
        }
    }

    private suspend fun handleReceivedMessage(message: String?) {
        log.info("Received msg: $message")

        if (EXPECTED_MSG == message) {
            FlightRoutesService.refreshFlightRoutes()
        } else {
            log.info("Unexpected message. Do nothing.")
        }
    }
}
