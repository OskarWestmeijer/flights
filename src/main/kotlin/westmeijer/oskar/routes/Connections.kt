package westmeijer.oskar.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import westmeijer.oskar.models.server.ConnectionsResponse
import westmeijer.oskar.services.ConnectionsService

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
