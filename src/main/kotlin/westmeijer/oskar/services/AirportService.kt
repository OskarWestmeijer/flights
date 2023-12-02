package westmeijer.oskar.services

import io.ktor.util.logging.*
import org.apache.commons.csv.CSVFormat
import westmeijer.oskar.models.Airport
import java.nio.file.Files
import java.nio.file.Paths


internal val log = KtorSimpleLogger("westmeijer.oskar.AirportService")

object AirportService {

    private var airportMap: Map<String, Airport> = emptyMap()

    fun getAirport(airportCode: String): Airport {
        if (airportMap.isEmpty()) {
            airportMap = readCsv()
        }

        return airportMap[airportCode]!!
    }

    private fun readCsv(): Map<String, Airport> {
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