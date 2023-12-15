package westmeijer.oskar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SecretsTest {

    @Test
    fun testSecrets() {
        val expectedKey = "12345"
        val expectedBaseUrl = "http://test.com"

        Secrets.apiKey = expectedKey
        Secrets.baseUrl = expectedBaseUrl

        assertEquals(expectedKey, Secrets.apiKey)
        assertEquals(expectedBaseUrl, Secrets.baseUrl)
    }

    @Test
    fun ensureSecretIsNotInit() {

        assertFailsWith<UninitializedPropertyAccessException>("lateinit property apiKey has not been initialized") {
            Secrets.apiKey
        }

        assertFailsWith<UninitializedPropertyAccessException>("lateinit property baseUrl has not been initialized") {
            Secrets.baseUrl
        }
    }


}