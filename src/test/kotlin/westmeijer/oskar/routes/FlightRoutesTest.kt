package westmeijer.oskar.routes

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import org.skyscreamer.jsonassert.JSONAssert
import kotlin.test.Test
import kotlin.test.assertEquals

class FlightRoutesTest {

    @Test
    fun testFlightRoutes() = testApplication {
        runTest {

            Thread.sleep(2000)
            val response = client.get("/flight-routes")

            val filePath = "expected_flight_routes.json"
            val inStream = ClassLoader.getSystemResourceAsStream(filePath)
            val expected = inStream!!.bufferedReader().use { it.readText() }.trimIndent()

            assertEquals(HttpStatusCode.OK, response.status)
            JSONAssert.assertEquals(expected, response.bodyAsText(), true)
        }
    }

}
