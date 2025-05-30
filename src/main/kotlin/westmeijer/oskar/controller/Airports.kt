package westmeijer.oskar.controller

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import westmeijer.oskar.controller.model.UnmappedAirportResponse
import westmeijer.oskar.service.airport.AirportService

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
