package westmeijer.oskar.services

import io.ktor.util.logging.*
import org.apache.commons.csv.CSVFormat
import westmeijer.oskar.models.Airport
import java.nio.file.Files
import java.nio.file.Paths


object AirportService {

    private val log = KtorSimpleLogger("westmeijer.oskar.services.AirportService")

    private val airportMap: Map<String, Airport>

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
            log.warn("No mapping for airportCode: $airportCode");
        }
        return airport
    }

    private fun readCsv(): Map<String, Airport> {
        log.info("entered csv reading")
        val uri = ClassLoader.getSystemResource("airports.csv").toURI()
        val inStream = Files.newInputStream(Paths.get(uri))

        return CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
            setIgnoreSurroundingSpaces(true)
        }.build().parse(inStream.reader()).drop(1).map {
            Airport(
                airportCode = it[2],
                latitude = it[5],
                longitude = it[6],
            )
        }.associateBy({ it.airportCode }, { it })
    }

}