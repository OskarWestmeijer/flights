package westmeijer.oskar.models

import westmeijer.oskar.models.server.Airport
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

}