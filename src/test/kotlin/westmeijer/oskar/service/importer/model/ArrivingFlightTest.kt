package westmeijer.oskar.service.importer.model

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ArrivingFlightTest {

    private val jsonFormat = Json { encodeDefaults = true } // ensures default fields are encoded

    @Test
    fun `test serialization of ArrivingFlight`() {
        val arrivingFlight = ArrivingFlight(
            "JFK",
            "flightNumber",
            "airlineName",
            "plannedArrivalTime"
        )

        val expectedJson =
            """{"originAirport3LCode":"JFK","flightnumber":"flightNumber","airlineName":"airlineName","plannedArrivalTime":"plannedArrivalTime"}"""

        val jsonResult = jsonFormat.encodeToString(arrivingFlight)

        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `test deserialization of ArrivingFlight`() {
        val json =
            """{"originAirport3LCode":"JFK","flightnumber":"flightNumber","airlineName":"airlineName","plannedArrivalTime":"plannedArrivalTime"}"""

        val expectedArrivingFlight = ArrivingFlight(
            "JFK",
            "flightNumber",
            "airlineName",
            "plannedArrivalTime"
        )

        val arrivingFlight = jsonFormat.decodeFromString<ArrivingFlight>(json)

        assertEquals(expectedArrivingFlight, arrivingFlight)
    }

    @Test
    fun `test deserialization of empty originAirport3LCode`() {
        val json =
            """{"originAirport3LCode":"","flightnumber":"flightNumber","airlineName":"airlineName","plannedArrivalTime":"plannedArrivalTime"}"""

        val expectedArrivingFlight = ArrivingFlight(
            "",
            "flightNumber",
            "airlineName",
            "plannedArrivalTime"
        )

        val arrivingFlight = jsonFormat.decodeFromString<ArrivingFlight>(json)

        assertEquals(expectedArrivingFlight, arrivingFlight)
    }

    @Test
    fun `test serialization of empty originAirport3LCode`() {
        val arrivingFlight = ArrivingFlight(
            "",
            "flightNumber",
            "airlineName",
            "plannedArrivalTime"
        )

        val expectedJson =
            """{"originAirport3LCode":"","flightnumber":"flightNumber","airlineName":"airlineName","plannedArrivalTime":"plannedArrivalTime"}"""

        val jsonResult = jsonFormat.encodeToString(arrivingFlight)

        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `test deserialization with missing field`() {
        val json = """{}""" // missing required fields

        assertFailsWith<SerializationException> {
            jsonFormat.decodeFromString<ArrivingFlight>(json)
        }
    }
}
