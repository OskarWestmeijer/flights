package westmeijer.oskar.services

import io.ktor.util.logging.*
import westmeijer.oskar.models.Destination
import westmeijer.oskar.models.FlightRoute

object FlightRoutesService {

    private val log = KtorSimpleLogger("westmeijer.oskar.services.FlightRoutesService")

    private var hamburgFlightRoutes: List<FlightRoute> = emptyList()

    suspend fun getFlightRoutes(): List<FlightRoute> {
        if (hamburgFlightRoutes.isEmpty()) {
            log.info("Flight routes is empty.")
            refreshFlightRoutes()
        }
        return hamburgFlightRoutes
    }

    suspend fun refreshFlightRoutes() {
        log.info("Start refreshing flight routes")
        val destinations: List<Destination> = HamAirportClient.getDestinations()
        hamburgFlightRoutes = map(destinations)
        log.info("Retrieved destinations count: ${destinations.size}, mapped flightRoutes count: ${hamburgFlightRoutes.size}")
        log.info("Finish refreshing flight routes")
    }

    private fun map(destinations: List<Destination>): List<FlightRoute> {
        return destinations
            .mapNotNull { AirportService.getAirport(it.destinationAirport3LCode) }
            .map {
                FlightRoute(1, AirportService.HAM_AIRPORT, it)
            }
    }

}