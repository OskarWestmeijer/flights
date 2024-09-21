package westmeijer.oskar.models.client

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

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

}