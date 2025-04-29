package westmeijer.oskar

import westmeijer.oskar.services.cache.SchedulerListener
import configureServerSerialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import westmeijer.oskar.services.cache.CacheService
import westmeijer.oskar.routes.registerAirports
import westmeijer.oskar.routes.registerConnections
import westmeijer.oskar.routes.registerOpenapi
import westmeijer.oskar.services.airport.AirportService
import westmeijer.oskar.services.importer.FlightsImportService

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    Secrets.apiKey = environment.config.property("api.key").getString()
    Secrets.baseUrl = environment.config.property("api.url").getString()
    Secrets.redisUrl = environment.config.property("redis.url").getString()

    configureServerSerialization()

    install(CORS) {
        allowHost("*")
        allowHeader(HttpHeaders.ContentType)
    }

    // register api endpoints
    registerConnections()
    registerAirports()
    registerOpenapi()

    // init airport csv
    AirportService.getAirport("HEL")

    // init redis cache
    CacheService

    val scope = CoroutineScope(Dispatchers.Default + CoroutineName("FlightsApiMainCoroutine"))
    scope.launch {
        try {
            FlightsImportService.import()
        } catch (e: Exception) {
            log.error("Error initializing flight routes. Cache init depending on scheduled refresh.", e)
        }
    }

    // start listening for scheduler
    SchedulerListener.startListening(scope)
}
