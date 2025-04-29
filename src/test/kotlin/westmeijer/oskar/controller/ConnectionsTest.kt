package westmeijer.oskar.controller

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.skyscreamer.jsonassert.JSONAssert
import kotlin.test.Test
import kotlin.test.assertEquals

class ConnectionsTest {

    @Test
    fun testFlightRoutes() = testApplication {

        environment {
            config = ApplicationConfig("application.yaml")
        }

        Thread.sleep(2000)
        val response = client.get("/connections")

        val filePath = "expected_connections.json"
        val inStream = ClassLoader.getSystemResourceAsStream(filePath)
        val expected = inStream!!.bufferedReader().use { it.readText() }.trimIndent()

        assertEquals(HttpStatusCode.OK, response.status)
        JSONAssert.assertEquals(expected, response.bodyAsText(), false)
    }

}
