package westmeijer.oskar.controller

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.registerOpenapi() {
    routing {

        singlePageApplication {
            useResources = true
            filesPath = "public"
            defaultPage = "index.html"
            ignoreFiles { it.endsWith(".txt") }
        }
    }
}
