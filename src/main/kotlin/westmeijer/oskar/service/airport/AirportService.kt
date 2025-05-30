package westmeijer.oskar.service.airport

import io.ktor.util.logging.*
import org.apache.commons.csv.CSVFormat
import westmeijer.oskar.service.airport.model.Airport


object AirportService {

    private val log = KtorSimpleLogger("westmeijer.oskar.services.airport.AirportService")

    private val airportMap: Map<String, Airport>

    val unmappedAirports: MutableSet<String> = mutableSetOf()

    val HAM_AIRPORT = Airport("HAM", "Hamburg Airport", "DE", "53.6304", "9.98823")

    init {
        airportMap = readCsv()
        log.info("Init AirportMap entries with count: ${airportMap.size}")
    }

    fun getAirport(airportCode: String): Airport? {
        if (airportMap.isEmpty()) {
            throw RuntimeException("Airport map is empty!")
        }

        val airport = airportMap[airportCode]
        if (airport == null) {
            unmappedAirports.add(airportCode)
            log.warn("No mapping for airportCode: $airportCode");
        }
        return airport
    }

    private fun readCsv(): Map<String, Airport> {
        val inStream = ClassLoader.getSystemResourceAsStream("airports.csv")

        return CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
            setIgnoreSurroundingSpaces(true)
        }.build().parse(inStream.reader()).drop(1).map {
            Airport(
                airportCode = it[2],
                airportName = it[4],
                countryCode = it[0],
                latitude = it[5],
                longitude = it[6]
            )
        }.associateBy({ it.airportCode }, { it })
    }

}