package westmeijer.oskar

import westmeijer.oskar.service.cache.SchedulerListener
import configureServerSerialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import westmeijer.oskar.service.cache.CacheService
import westmeijer.oskar.controller.registerAirports
import westmeijer.oskar.controller.registerConnections
import westmeijer.oskar.controller.registerOpenapi
import westmeijer.oskar.service.airport.AirportService
import westmeijer.oskar.service.importer.FlightsImportService

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
