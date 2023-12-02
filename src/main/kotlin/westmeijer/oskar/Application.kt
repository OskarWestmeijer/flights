package westmeijer.oskar

import configureSerialization
import io.ktor.http.*
import io.ktor.server.application.*
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
    configureSerialization()
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
