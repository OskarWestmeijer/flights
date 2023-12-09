package westmeijer.oskar

import configureServerSerialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import westmeijer.oskar.routes.registerFlightRoutes
import westmeijer.oskar.services.AirportService
import westmeijer.oskar.services.FlightRoutesService
import java.time.Duration
import java.time.temporal.ChronoUnit

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    Secrets.apiKey = environment.config.property("api.key").getString()
    Secrets.baseUrl = environment.config.property("api.url").getString()

    configureServerSerialization()

    install(CORS) {
        allowHost("*")
        allowHeader(HttpHeaders.ContentType)
    }

    registerFlightRoutes()

    // init with csv after startup
    AirportService.getAirport("HEL")

    // refresh flight-routes job
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        while (true) {
            FlightRoutesService.refreshFlightRoutes()
            delay(Duration.of(1, ChronoUnit.HOURS))
        }
    }
}
