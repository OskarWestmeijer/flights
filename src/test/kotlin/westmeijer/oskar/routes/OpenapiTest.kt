package westmeijer.oskar.routes

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenapiTest {

    @Test
    fun testOpenapi() = testApplication {
        runTest {

            val actual = client.get("/")

            assertEquals(HttpStatusCode.OK, actual.status)
        }
    }

}