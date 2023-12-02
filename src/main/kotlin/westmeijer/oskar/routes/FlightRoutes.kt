package westmeijer.oskar.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import westmeijer.oskar.models.Airport
import westmeijer.oskar.models.FlightRoute
import westmeijer.oskar.models.FlightRouteResponse

fun Application.registerFlightRoutes() {
    routing {
        getFlightRoutes()
    }
}


fun Route.getFlightRoutes() {
    get("/routes") {
        val hel = Airport("HEL", "60.192059", "24.945831")
        val ham = Airport("HAM", "51.5103", "7.49347")
        val flightRoute = FlightRoute(1, hel, ham)
        val flightRouteResponse = FlightRouteResponse(listOf(flightRoute))

        call.respond(flightRouteResponse)
    }
}
