package westmeijer.oskar

import configureServerSerialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import westmeijer.oskar.routes.registerFlightRoutes

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    val config = ApplicationConfig("application.yaml")
    Secrets.apiKey = config.propertyOrNull("api.key")!!.getString()
    Secrets.baseUrl = config.propertyOrNull("api.url")!!.getString()

    configureServerSerialization()
    install(CORS) {
        allowHost("*")
        allowHeader(HttpHeaders.ContentType)
    }
    routing {
        get("/") {
            call.respondText { "Hello from maps backend!" }
        }
    }
    registerFlightRoutes()
}
