package westmeijer.oskar.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import westmeijer.oskar.models.Airport
import westmeijer.oskar.models.Route
import westmeijer.oskar.models.RoutesResponse

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/routes") {
            val hel = Airport("HEL", "60.192059", "24.945831")
            val ham = Airport("HAM", "51.5103", "7.49347")
            val route = Route(1, hel, ham)
            val routesResponse = RoutesResponse(listOf(route))

            call.respond(routesResponse)
        }
    }
}
