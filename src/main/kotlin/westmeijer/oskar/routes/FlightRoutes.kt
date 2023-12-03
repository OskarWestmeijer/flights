package westmeijer.oskar.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import westmeijer.oskar.models.FlightRouteResponse
import westmeijer.oskar.services.FlightRoutesService

internal val log = KtorSimpleLogger("westmeijer.oskar.routes.FlightRoutes")

fun Application.registerFlightRoutes() {
    routing {
        getFlightRoutes()
    }
}


fun Route.getFlightRoutes() {
    get("/flight-routes") {

        val flightRoutes = FlightRoutesService.getFlightRoutes()
        val flightRouteResponse = FlightRouteResponse(flightRoutes)
        log.info(flightRouteResponse.toString())

        call.respond(flightRouteResponse)
    }
}
