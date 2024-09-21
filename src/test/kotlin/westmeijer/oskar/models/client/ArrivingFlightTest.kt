package westmeijer.oskar.models.client

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrivingFlightTest {

    @Test
    fun `test serialization of ArrivingFlight`() {
        // Arrange
        val arrivingFlight = ArrivingFlight("JFK")
        val expectedJson = """{"originAirport3LCode":"JFK"}"""

        // Act
        val jsonResult = Json.encodeToString(arrivingFlight)

        // Assert
        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `test deserialization of ArrivingFlight`() {
        // Arrange
        val json = """{"originAirport3LCode":"JFK"}"""
        val expectedArrivingFlight = ArrivingFlight("JFK")

        // Act
        val arrivingFlight = Json.decodeFromString<ArrivingFlight>(json)

        // Assert
        assertEquals(expectedArrivingFlight, arrivingFlight)
    }

}