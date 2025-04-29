package westmeijer.oskar.services.importer.model

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DepartingFlightTest {

    @Test
    fun `test serialization of DepartingFlight`() {
        // Arrange
        val departingFlight = DepartingFlight("JFK")
        val expectedJson = """{"destinationAirport3LCode":"JFK"}"""

        // Act
        val jsonResult = Json.encodeToString(departingFlight)

        // Assert
        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `test deserialization of DepartingFlight`() {
        // Arrange
        val json = """{"destinationAirport3LCode":"JFK"}"""
        val expectedDepartingFlight = DepartingFlight("JFK")

        // Act
        val departingFlight = Json.decodeFromString<DepartingFlight>(json)

        // Assert
        assertEquals(expectedDepartingFlight, departingFlight)
    }

    @Test
    fun `test deserialization of empty destinationAirport3LCode`() {
        // Arrange
        val json = """{"destinationAirport3LCode":""}"""
        val expectedDepartingFlight = DepartingFlight("")

        // Act
        val departingFlight = Json.decodeFromString<DepartingFlight>(json)

        // Assert
        assertEquals(expectedDepartingFlight, departingFlight)
    }

    @Test
    fun `test serialization of empty destinationAirport3LCode`() {
        // Arrange
        val departingFlight = DepartingFlight("")
        val expectedJson = """{"destinationAirport3LCode":""}"""

        // Act
        val jsonResult = Json.encodeToString(departingFlight)

        // Assert
        assertEquals(expectedJson, jsonResult)
    }

    @Test
    fun `test deserialization with missing destinationAirport3LCode`() {
        // Arrange
        val json = """{}""" // missing the "destinationAirport3LCode" field

        // Act & Assert
        assertFailsWith<SerializationException> {
            Json.decodeFromString<DepartingFlight>(json)
        }
    }
}
