package westmeijer.oskar

import configureServerSerialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import westmeijer.oskar.redis.Cache
import westmeijer.oskar.redis.SchedulerListener
import westmeijer.oskar.routes.registerAirports
import westmeijer.oskar.routes.registerFlightRoutes
import westmeijer.oskar.routes.registerOpenapi
import westmeijer.oskar.services.AirportService
import westmeijer.oskar.services.FlightRoutesService

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
    registerFlightRoutes()
    registerAirports()
    registerOpenapi()

    // init airport csv
    AirportService.getAirport("HEL")

    // init redis cache
    Cache

    // init flight-routes
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        FlightRoutesService.refreshFlightRoutes()
    }

    // start listening for scheduler
    scope.launch {
        SchedulerListener.startListening()
    }
}
