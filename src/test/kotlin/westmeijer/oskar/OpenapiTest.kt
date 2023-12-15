package westmeijer.oskar

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenapiTest {

    @Test
    fun testOpenapi() = testApplication {
        val actual = client.get("/")

        assertEquals(HttpStatusCode.OK, actual.status)
    }

}