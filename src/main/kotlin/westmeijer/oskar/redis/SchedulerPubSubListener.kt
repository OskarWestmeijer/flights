package westmeijer.oskar.redis

import io.ktor.util.logging.*
import io.lettuce.core.pubsub.RedisPubSubListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import westmeijer.oskar.services.FlightRoutesService

class SchedulerPubSubListener : RedisPubSubListener<String, String> {

    private val log = KtorSimpleLogger("westmeijer.oskar.cache.SchedulerPubSubListener")

    override fun message(channel: String?, message: String?) {
        log.info("Received from: $channel, msg: $message")

        if (Redis.SCHEDULER_CHANNEL == channel && Redis.EXPECTED_MSG == message) {
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                FlightRoutesService.refreshFlightRoutes()
            }
        } else {
            log.info("Unexpected channel or message. Do nothing.")
        }

    }

    override fun message(pattern: String?, channel: String?, message: String?) {
        TODO("Not yet implemented")
    }

    override fun subscribed(channel: String?, count: Long) {
        log.info("Subscribed to: $channel, count: $count")
    }

    override fun psubscribed(pattern: String?, count: Long) {
        TODO("Not yet implemented")
    }

    override fun unsubscribed(channel: String?, count: Long) {
        TODO("Not yet implemented")
    }

    override fun punsubscribed(pattern: String?, count: Long) {
        TODO("Not yet implemented")
    }
}