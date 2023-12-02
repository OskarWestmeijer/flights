package westmeijer.oskar.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import westmeijer.oskar.models.FlightRoute
import westmeijer.oskar.models.FlightRouteResponse
import westmeijer.oskar.services.AirportService

internal val log = KtorSimpleLogger("westmeijer.oskar.routes.FlightRoutes")

fun Application.registerFlightRoutes() {
    routing {
        getFlightRoutes()
    }
}


fun Route.getFlightRoutes() {
    get("/flight-routes") {
        val hel = AirportService.getAirport("HEL")
        val ham = AirportService.getAirport("HAM")

        val flightRoute = FlightRoute(1, hel, ham)
        val flightRouteResponse = FlightRouteResponse(listOf(flightRoute))

        log.info(flightRouteResponse.toString())

        call.respond(flightRouteResponse)
    }
}
