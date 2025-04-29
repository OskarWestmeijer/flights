package westmeijer.oskar.services.importer

import io.ktor.util.logging.*
import westmeijer.oskar.services.airport.AirportService
import westmeijer.oskar.services.airport.model.AirportCode
import westmeijer.oskar.services.cache.CacheService
import westmeijer.oskar.services.connections.model.Connection
import westmeijer.oskar.services.importer.model.ArrivingFlight
import westmeijer.oskar.services.importer.model.DepartingFlight
import java.time.Instant
import java.time.temporal.ChronoUnit

internal object FlightsImportService {

    private val log = KtorSimpleLogger("westmeijer.oskar.services.importer.ImportService")

    suspend fun import() {
        log.info("Start refreshing connections")
        val importedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()

        val departingFlights: List<DepartingFlight> = HamAirportApiClient.getDepartingFlights()
        val arrivingFlights: List<ArrivingFlight> = HamAirportApiClient.getArrivingFlights()

        val departingFlightsMap: Map<AirportCode, Int> = aggregateDepartingFlights(departingFlights)
        val arrivingFlightsMap: Map<AirportCode, Int> = aggregateArrivingFlights(arrivingFlights)

        val hamburgConnections: List<Connection> = map(arrivingFlightsMap, departingFlightsMap, importedAt)

        CacheService.setCache(CacheService.CONNECTIONS_KEY, hamburgConnections)

        log.info("Departing flights count: ${departingFlights.size}, departing connections count: ${departingFlightsMap.size}")
        log.info("Arriving flights count: ${arrivingFlights.size}, arriving connections count: ${arrivingFlightsMap.size}")
        log.info("Total connections count: ${hamburgConnections.size}, importedAt: $importedAt")
        log.info("Finish refreshing connections")
    }

    private fun map(
        arrivingFlightsMap: Map<AirportCode, Int>,
        departingFlightsMap: Map<AirportCode, Int>,
        importedAt: String
    ): List<Connection> {

        val airportKeys: Set<AirportCode> = departingFlightsMap.keys.plus(arrivingFlightsMap.keys)

        return airportKeys
            .filter { AirportService.getAirport(it.code) != null }
            .map {
                val departureCount = departingFlightsMap.get(it) ?: 0
                val arrivalCount = arrivingFlightsMap.get(it) ?: 0
                Connection(
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