package westmeijer.oskar.routes

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenapiTest {

    @Test
    fun testOpenapi() = testApplication {

        environment {
            config = ApplicationConfig("application.yaml")
        }

        val actual = client.get("/")

        assertEquals(HttpStatusCode.OK, actual.status)
    }

}