package westmeijer.oskar.service.importer

import io.ktor.util.logging.*
import westmeijer.oskar.service.airport.AirportService
import westmeijer.oskar.service.airport.model.AirportCode
import westmeijer.oskar.service.cache.CacheService
import westmeijer.oskar.service.connections.model.Connection
import westmeijer.oskar.service.connections.model.Flight
import westmeijer.oskar.service.connections.model.FlightType
import westmeijer.oskar.service.importer.model.ArrivingFlight
import westmeijer.oskar.service.importer.model.DepartingFlight
import java.time.Instant
import java.time.temporal.ChronoUnit

internal object FlightsImportService {

    private val log = KtorSimpleLogger("westmeijer.oskar.services.importer.ImportService")

    suspend fun import() {
        log.info("Start refreshing connections")
        val importedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()

        val departingFlights: List<DepartingFlight> = HamAirportApiClient.getDepartingFlights()
        val arrivingFlights: List<ArrivingFlight> = HamAirportApiClient.getArrivingFlights()

        val departingFlightsMap: Map<AirportCode, List<Flight>> = aggregateDepartingFlights(departingFlights)
        val arrivingFlightsMap: Map<AirportCode, List<Flight>> = aggregateArrivingFlights(arrivingFlights)

        val hamburgConnections: List<Connection> = map(arrivingFlightsMap, departingFlightsMap, importedAt)

        CacheService.setCache(CacheService.CONNECTIONS_KEY, hamburgConnections)

        log.info("Departing flights count: ${departingFlights.size}, departing connections count: ${departingFlightsMap.size}")
        log.info("Arriving flights count: ${arrivingFlights.size}, arriving connections count: ${arrivingFlightsMap.size}")
        log.info("Total connections count: ${hamburgConnections.size}, importedAt: $importedAt")
        log.info("Finish refreshing connections")
    }

    private fun map(
        arrivingFlightsMap: Map<AirportCode, List<Flight>>,
        departingFlightsMap: Map<AirportCode, List<Flight>>,
        importedAt: String
    ): List<Connection> {

        // union of all airports that have arrivals or departures
        val airportKeys: Set<AirportCode> = departingFlightsMap.keys + arrivingFlightsMap.keys

        return airportKeys
            .mapNotNull { airportCode ->
                val airport = AirportService.getAirport(airportCode.code) ?: return@mapNotNull null

                val departingFlights = departingFlightsMap[airportCode].orEmpty()
                val arrivingFlights = arrivingFlightsMap[airportCode].orEmpty()
                val allFlights = departingFlights + arrivingFlights

                Connection(
                    hamAirport = AirportService.HAM_AIRPORT,
                    connectionAirport = airport,
                    departureFlightCount = departingFlights.size,
                    arrivalFlightCount = arrivingFlights.size,
                    totalFlightCount = allFlights.size,
                    importedAt = importedAt,
                    flights = allFlights
                )
            }
            .sortedByDescending { it.totalFlightCount }
    }

    private fun aggregateDepartingFlights(departingFlights: List<DepartingFlight>): Map<AirportCode, List<Flight>> {
        return departingFlights
            .groupBy { AirportCode(it.destinationAirport3LCode) }
            .mapValues { (_, flights) ->
                flights.map { df ->
                    Flight(
                        flightType = FlightType.DEPARTURE_HAM,
                        flightNumber = df.flightNumber,
                        airlineName = df.airlineName,
                        plannedTime = df.plannedDepartureTime
                    )
                }
            }
    }


    private fun aggregateArrivingFlights(arrivingFlights: List<ArrivingFlight>): Map<AirportCode, List<Flight>> {
        return arrivingFlights
            .groupBy { AirportCode(it.originAirport3LCode) }
            .mapValues { (_, flights) ->
                flights.map { af ->
                    Flight(
                        flightType = FlightType.ARRIVAL_HAM,
                        flightNumber = af.flightNumber,
                        airlineName = af.airlineName,
                        plannedTime = af.plannedArrivalTime
                    )
                }
            }
    }

}