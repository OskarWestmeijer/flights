package westmeijer.oskar.redis

import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands

object PubSubListener {

    private val pubSubConnection: StatefulRedisPubSubConnection<String, String>
    private val pubsubCommands: RedisPubSubCommands<String, String>
    const val SCHEDULER_CHANNEL = "refresh_routes"
    const val EXPECTED_MSG = "Refresh them routes."

    init {
        pubSubConnection = RedisClient.client.connectPubSub()
        pubSubConnection.addListener(SchedulerListenerImpl())
        pubsubCommands = pubSubConnection.sync()
        pubsubCommands.subscribe(SCHEDULER_CHANNEL)
    }

}