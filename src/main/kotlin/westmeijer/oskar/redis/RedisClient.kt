package westmeijer.oskar.redis

import io.ktor.util.logging.*
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import westmeijer.oskar.Secrets

object RedisClient {

    private val log = KtorSimpleLogger("westmeijer.oskar.cache.RedisClient")
    val client: RedisClient

    init {
        val redisUri = RedisURI.Builder.redis(Secrets.redisUrl).build()
        client = RedisClient.create(redisUri)
        log.info("Connected to redis, uri: $redisUri")
    }
}