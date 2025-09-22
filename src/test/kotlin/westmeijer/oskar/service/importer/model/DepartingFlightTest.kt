package westmeijer.oskar.service.importer.model

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DepartingFlightTest {

    private val jsonFormat = Json { encodeDefaults = true }

    @Test
    fun `test serialization of DepartingFlight`() {
        val departingFlight = DepartingFlight(
            "JFK",
            "flightNumber",
            "airlineName",
            "plannedDepartureTime"
        )

        val expectedJson =
            """{"destinationAirport3LCode":"JFK","flightnumber":"flightNumber","airlineName":"airlineName","plannedDepartureTime":"plannedDepartureTime"}"""

        val jsonResult = jsonFormat.encodeToString(departingFlight)

        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `test deserialization of DepartingFlight`() {
        val json =
            """{"destinationAirport3LCode":"JFK","flightnumber":"flightNumber","airlineName":"airlineName","plannedDepartureTime":"plannedDepartureTime"}"""

        val expectedDepartingFlight = DepartingFlight(
            "JFK",
            "flightNumber",
            "airlineName",
            "plannedDepartureTime"
        )

        val departingFlight = jsonFormat.decodeFromString<DepartingFlight>(json)

        assertEquals(expectedDepartingFlight, departingFlight)
    }

    @Test
    fun `test deserialization of empty destinationAirport3LCode`() {
        val json =
            """{"destinationAirport3LCode":"","flightnumber":"flightNumber","airlineName":"airlineName","plannedDepartureTime":"plannedDepartureTime"}"""

        val expectedDepartingFlight = DepartingFlight(
            "",
            "flightNumber",
            "airlineName",
            "plannedDepartureTime"
        )

        val departingFlight = jsonFormat.decodeFromString<DepartingFlight>(json)

        assertEquals(expectedDepartingFlight, departingFlight)
    }

    @Test
    fun `test serialization of empty destinationAirport3LCode`() {
        val departingFlight = DepartingFlight(
            "",
            "flightNumber",
            "airlineName",
            "plannedDepartureTime"
        )

        val expectedJson =
            """{"destinationAirport3LCode":"","flightnumber":"flightNumber","airlineName":"airlineName","plannedDepartureTime":"plannedDepartureTime"}"""

        val jsonResult = jsonFormat.encodeToString(departingFlight)

        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `test deserialization with missing destinationAirport3LCode`() {
        val json = """{}""" // missing required fields

        assertFailsWith<SerializationException> {
            jsonFormat.decodeFromString<DepartingFlight>(json)
        }
    }
}

