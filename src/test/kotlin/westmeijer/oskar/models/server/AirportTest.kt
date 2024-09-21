package westmeijer.oskar.models.server

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AirportTest {

    @Test
    fun shouldThrowOnBlankAirportCode() {
        val exception = assertFailsWith<IllegalArgumentException>(
            block = {
                Airport("", "Hamburg Airport", "DE", "53.6304", "9.98823")
            }
        )
        assertEquals("airportCode must not be blank", exception.message)
    }

    @Test
    fun shouldThrowOnBlankAirportName() {
        val exception = assertFailsWith<IllegalArgumentException>(
            block = {
                Airport("HAM", "", "DE", "53.6304", "9.98823")
            }
        )
        assertEquals("airportName must not be blank", exception.message)
    }

    @Test
    fun shouldThrowOnBlankCountryCode() {
        val exception = assertFailsWith<IllegalArgumentException>(
            block = {
                Airport("HAM", "Hamburg Airport", "", "53.6304", "9.98823")
            }
        )
        assertEquals("countryCode must not be blank", exception.message)
    }

    @Test
    fun shouldThrowOnBlankLatitude() {
        val exception = assertFailsWith<IllegalArgumentException>(
            block = {
                Airport("HAM", "Hamburg Airport", "DE", "", "9.98823")
            }
        )
        assertEquals("latitude must not be blank", exception.message)
    }

    @Test
    fun shouldThrowOnBlankLongitude() {
        val exception = assertFailsWith<IllegalArgumentException>(
            block = {
                Airport("HAM", "Hamburg Airport", "DE", "53.6304", "")
            }
        )
        assertEquals("longitude must not be blank", exception.message)
    }

    @Test
    fun shouldCreateValidAirport() {
        // Arrange & Act
        val airport = Airport("HAM", "Hamburg Airport", "DE", "53.6304", "9.98823")

        // Assert
        assertEquals("HAM", airport.airportCode)
        assertEquals("Hamburg Airport", airport.airportName)
        assertEquals("DE", airport.countryCode)
        assertEquals("53.6304", airport.latitude)
        assertEquals("9.98823", airport.longitude)
    }
}
