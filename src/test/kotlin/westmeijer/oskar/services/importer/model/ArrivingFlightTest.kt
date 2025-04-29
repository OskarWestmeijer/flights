package westmeijer.oskar.services.importer.model

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

    @Test
    fun `test deserialization of empty originAirport3LCode`() {
        // Arrange
        val json = """{"originAirport3LCode":""}"""
        val expectedArrivingFlight = ArrivingFlight("")

        // Act
        val arrivingFlight = Json.decodeFromString<ArrivingFlight>(json)

        // Assert
        assertEquals(expectedArrivingFlight, arrivingFlight)
    }

    @Test
    fun `test serialization of empty originAirport3LCode`() {
        // Arrange
        val arrivingFlight = ArrivingFlight("")
        val expectedJson = """{"originAirport3LCode":""}"""

        // Act
        val jsonResult = Json.encodeToString(arrivingFlight)

        // Assert
        assertEquals(expectedJson, jsonResult)
    }


    @Test
    fun `test deserialization with missing field`() {
        // Arrange
        val json = """{}""" // missing the "originAirport3LCode"

        // Act & Assert
        assertFailsWith<SerializationException> {
            Json.decodeFromString<ArrivingFlight>(json)
        }
    }


}