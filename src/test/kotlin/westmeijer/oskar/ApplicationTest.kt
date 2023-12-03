package westmeijer.oskar

import io.ktor.server.testing.*
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    @Ignore
    fun testRoot() = testApplication {
        assertEquals("a", "a")
    }

}
