package westmeijer.oskar.redis

import io.ktor.util.logging.*
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import westmeijer.oskar.services.FlightRoutesService

object SchedulerListener {

    private val log = KtorSimpleLogger("westmeijer.oskar.redis.SchedulerListener")

    private val redisCommands: RedisCommands<String, String>
    const val SCHEDULER_CHANNEL = "refresh_routes"
    const val EXPECTED_MSG = "Refresh them routes."

    init {
        redisCommands = RedisClient.client.connect().sync()
    }

    fun startListening() {
        // Start listening for messages in a blocking manner
        while (true) {
            val message = redisCommands.spop(SCHEDULER_CHANNEL)  // Blocking call
            if (message != null) {
                CoroutineScope(Dispatchers.Default).launch {
                    handleReceivedMessage(message)
                }
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