package westmeijer.oskar.services.cache

import io.ktor.util.logging.*
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import westmeijer.oskar.services.importer.FlightsImportService

object SchedulerListener {

    private val log = KtorSimpleLogger("westmeijer.oskar.redis.westmeijer.oskar.services.cache.SchedulerListener")

    private val redisCommands: RedisCommands<String, String> = RedisClient.client.connect().sync()
    private const val SCHEDULER_SET = "refresh_routes"
    private const val EXPECTED_MSG = "Refresh them routes."

    fun startListening(scope: CoroutineScope) {
        scope.launch {
            log.info("Starting scheduler loop.")
            while (isActive) {
                try {
                    val message = redisCommands.spop(SCHEDULER_SET)
                    if (message != null) {
                        handleReceivedMessage(message)
                    } else {
                        delay(10000)
                    }
                } catch (e: Exception) {
                    log.error("Error inside scheduler loop. isActive: $isActive", e)
                    delay(10000)
                }
            }
            log.info("Exiting scheduler loop.")
        }
    }

    private suspend fun handleReceivedMessage(message: String?) {
        log.info("Received msg: $message")

        if (EXPECTED_MSG == message) {
            FlightsImportService.import()
        } else {
            log.info("Unexpected message. Do nothing.")
        }
    }
}
