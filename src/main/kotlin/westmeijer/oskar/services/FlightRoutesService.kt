package westmeijer.oskar.services

import io.ktor.util.logging.*
import westmeijer.oskar.models.client.ArrivingFlight
import westmeijer.oskar.models.client.DepartingFlight
import westmeijer.oskar.models.server.AirportCode
import westmeijer.oskar.models.server.FlightRoute
import westmeijer.oskar.redis.Cache
import java.time.Instant
import java.time.temporal.ChronoUnit

object FlightRoutesService {

    private val log = KtorSimpleLogger("westmeijer.oskar.services.FlightRoutesService")

    fun getFlightRoutes(): List<FlightRoute> {
        return Cache.getCache(Cache.FLIGHT_ROUTES_KEY)
    }

    suspend fun refreshFlightRoutes() {
        log.info("Start refreshing flight routes")
        val importedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()

        val departingFlights: List<DepartingFlight> = HamAirportClient.getDepartingFlights()
        val arrivingFlights: List<ArrivingFlight> = HamAirportClient.getArrivingFlights()

        val departingFlightsMap: Map<AirportCode, Int> = aggregateDepartingFlights(departingFlights)
        val arrivingFlightsMap: Map<AirportCode, Int> = aggregateArrivingFlights(arrivingFlights)

        val hamburgFlightRoutes: List<FlightRoute> = map(arrivingFlightsMap, departingFlightsMap, importedAt)

        Cache.setCache(Cache.FLIGHT_ROUTES_KEY, hamburgFlightRoutes)

        log.info("Departing flights count: ${departingFlights.size}, departing connections count: ${departingFlightsMap.size}")
        log.info("Arriving flights count: ${arrivingFlights.size}, arriving connections count: ${arrivingFlightsMap.size}")
        log.info("Total connections count: ${hamburgFlightRoutes.size}, importedAt: $importedAt")
        log.info("Finish refreshing flight routes")
    }

    private fun map(
        arrivingFlightsMap: Map<AirportCode, Int>,
        departingFlightsMap: Map<AirportCode, Int>,
        importedAt: String
    ): List<FlightRoute> {

        val airportKeys: Set<AirportCode> = departingFlightsMap.keys.plus(arrivingFlightsMap.keys)

        return airportKeys
            .filter { AirportService.getAirport(it.code) != null }
            .map {
                val departureCount = departingFlightsMap.get(it) ?: 0
                val arrivalCount = arrivingFlightsMap.get(it) ?: 0
                FlightRoute(
                    AirportService.HAM_AIRPORT,
                    AirportService.getAirport(it.code)!!,
                    departureCount,
                    arrivalCount,
                    departureCount + arrivalCount,
                    importedAt
                )
            }
            .sortedByDescending { it.totalFlightCount }
    }

    private fun aggregateDepartingFlights(departingFlights: List<DepartingFlight>): Map<AirportCode, Int> {
        return departingFlights
            .map { AirportCode(it.destinationAirport3LCode) }
            .groupingBy { it }
            .eachCount()
    }

    private fun aggregateArrivingFlights(arrivingFlights: List<ArrivingFlight>): Map<AirportCode, Int> {
        return arrivingFlights
            .map { AirportCode(it.originAirport3LCode) }
            .groupingBy { it }
            .eachCount()
    }

}