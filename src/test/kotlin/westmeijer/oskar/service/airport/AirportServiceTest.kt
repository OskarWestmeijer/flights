package westmeijer.oskar.service.airport

import westmeijer.oskar.service.airport.model.Airport
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class AirportServiceTest {

    @BeforeTest
    fun cleanup() {
        AirportService.unmappedAirports.clear()
    }

    @Test
    fun testGetAirport() {
        val expected = Airport("HEL", "Helsinki Airport (Helsinki-Vantaa Airport)", "FI", "60.3172", "24.9633")
        val actual = AirportService.getAirport("HEL")
        assertEquals(expected, actual)
        assertEquals(mutableSetOf(), AirportService.unmappedAirports)
    }

    @Test
    fun testHamAirport() {
        assertEquals(Airport("HAM", "Hamburg Airport", "DE", "53.6304", "9.98823"), AirportService.HAM_AIRPORT)
    }

    @Test
    fun testUnmappedAirports() {
        val expectedCode = "not_existing"

        val actual = AirportService.getAirport("not_existing")

        assertNull(actual)
        assertEquals(mutableSetOf(expectedCode), AirportService.unmappedAirports)
    }

}