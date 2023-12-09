package westmeijer.oskar.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import westmeijer.oskar.models.FlightRouteResponse
import westmeijer.oskar.services.FlightRoutesService

fun Application.registerFlightRoutes() {
    routing {
        getFlightRoutes()
    }
}

fun Route.getFlightRoutes() {
    get("/flight-routes") {
        val flightRoutes = FlightRoutesService.getFlightRoutes()
        val flightRouteResponse = FlightRouteResponse(flightRoutes)
        call.respond(flightRouteResponse)
    }
}
