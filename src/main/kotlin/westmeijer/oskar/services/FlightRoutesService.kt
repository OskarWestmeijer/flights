package westmeijer.oskar.services

import io.ktor.util.logging.*
import westmeijer.oskar.models.AirportCode
import westmeijer.oskar.models.DepartingFlight
import westmeijer.oskar.models.FlightRoute
import westmeijer.oskar.redis.Cache

object FlightRoutesService {

    private val log = KtorSimpleLogger("westmeijer.oskar.services.FlightRoutesService")

    fun getFlightRoutes(): List<FlightRoute> {
        return Cache.getCache(Cache.FLIGHT_ROUTES_KEY)
    }

    suspend fun refreshFlightRoutes() {
        log.info("Start refreshing flight routes")
        val departingFlights: List<DepartingFlight> = HamAirportClient.getDepartingFlights()
        val flights: Map<AirportCode, Int> = aggregateFlights(departingFlights)
        val hamburgFlightRoutes = map(flights)

        Cache.setCache(Cache.FLIGHT_ROUTES_KEY, hamburgFlightRoutes)

        log.info("Departing flights count: ${departingFlights.size}, mapped flight routes count: ${hamburgFlightRoutes.size}")
        log.info("Finish refreshing flight routes")
    }

    private fun map(flights: Map<AirportCode, Int>): List<FlightRoute> {
        return flights
            .filterKeys { AirportService.getAirport(it.code) != null }
            .map {
                FlightRoute(AirportService.HAM_AIRPORT, AirportService.getAirport(it.key.code)!!, it.value)
            }
            .sortedByDescending { it.flightCount }
    }

    private fun aggregateFlights(departingFlights: List<DepartingFlight>): Map<AirportCode, Int> {
        return departingFlights
            .map { AirportCode(it.destinationAirport3LCode) }
            .groupingBy { it }
            .eachCount()
    }

}