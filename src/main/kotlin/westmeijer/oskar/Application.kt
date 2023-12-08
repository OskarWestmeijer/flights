package westmeijer.oskar

import configureServerSerialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.cors.routing.*
import westmeijer.oskar.routes.registerFlightRoutes
import westmeijer.oskar.services.AirportService

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    val envFile = when (System.getenv("BACKEND_ENV")) {
        "production" -> "application-prod.yaml"
        else -> "application.yaml"
    }

    val config = ApplicationConfig(envFile)
    Secrets.apiKey = config.propertyOrNull("api.key")!!.getString()
    Secrets.baseUrl = config.propertyOrNull("api.url")!!.getString()

    configureServerSerialization()

    install(CORS) {
        allowHost("*")
        allowHeader(HttpHeaders.ContentType)
    }

    registerFlightRoutes()

    // init services after startup
    AirportService.getAirport("HEL")
}
