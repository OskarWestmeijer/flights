package westmeijer.oskar.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import westmeijer.oskar.routes.model.UnmappedAirportResponse
import westmeijer.oskar.services.airport.AirportService

fun Application.registerAirports() {
    routing {
        getAirports()
    }
}

fun Route.getAirports() {
    get("/airports") {

        if (call.request.queryParameters["unmapped"] != null) {
            call.respond(UnmappedAirportResponse(AirportService.unmappedAirports))
        }
        call.respond("Not yet implemented. Request with query param 'unmapped'.")
    }
}
