package westmeijer.oskar.controller

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import westmeijer.oskar.controller.model.ConnectionsResponse
import westmeijer.oskar.service.connections.ConnectionsService

fun Application.registerConnections() {
    routing {
        getConnections()
    }
}

fun Route.getConnections() {
    get("/connections") {
        val flightRoutes = ConnectionsService.getConnections()
        val connectionsResponse = ConnectionsResponse(flightRoutes, flightRoutes.first().importedAt)
        call.respond(connectionsResponse)
    }
}
