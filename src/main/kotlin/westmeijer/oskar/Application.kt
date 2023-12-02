package westmeijer.oskar

import configureSerialization
import io.ktor.http.*
import westmeijer.oskar.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.CORS

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureSerialization()
    install(CORS) {
        allowHost("*")
        allowHeader(HttpHeaders.ContentType)
    }
}
