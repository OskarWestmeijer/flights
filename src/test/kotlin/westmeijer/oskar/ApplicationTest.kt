package westmeijer.oskar

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.skyscreamer.jsonassert.JSONAssert
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        val response = client.get("/flight-routes")

        val filePath = "expected_flight_routes.json" // Replace with your JSON file path
        val inStream = ClassLoader.getSystemResourceAsStream(filePath)
        val expected = inStream!!.bufferedReader().use { it.readText() }.trimIndent()

        assertEquals(HttpStatusCode.OK, response.status)
        JSONAssert.assertEquals(expected, response.bodyAsText(), true)
    }

}
