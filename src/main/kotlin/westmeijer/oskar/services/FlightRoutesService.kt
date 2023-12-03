package westmeijer.oskar.services

import westmeijer.oskar.models.FlightRoute

object FlightRoutesService {

    private var hamburgFlightRoutes: List<FlightRoute> = emptyList()

    suspend fun getFlightRoutes(): List<FlightRoute> {
        if (hamburgFlightRoutes.isEmpty()) {
            hamburgFlightRoutes = HamAirportClient.requestApi()
        }
        return hamburgFlightRoutes
    }

}