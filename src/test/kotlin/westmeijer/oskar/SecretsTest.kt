package westmeijer.oskar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SecretsTest {

    @Test
    fun testSecrets() {
        val expectedKey = "12345"
        val expectedBaseUrl = "http://test.com"
        val expectedRedisUrl = "redis"

        Secrets.apiKey = expectedKey
        Secrets.baseUrl = expectedBaseUrl
        Secrets.redisUrl = expectedRedisUrl

        assertEquals(expectedKey, Secrets.apiKey)
        assertEquals(expectedBaseUrl, Secrets.baseUrl)
        assertEquals(expectedRedisUrl, Secrets.redisUrl)
    }

    @Test
    fun ensureSecretIsNotInit() {

        assertFailsWith<UninitializedPropertyAccessException>("lateinit property apiKey has not been initialized") {
            Secrets.apiKey
        }

        assertFailsWith<UninitializedPropertyAccessException>("lateinit property baseUrl has not been initialized") {
            Secrets.baseUrl
        }


        assertFailsWith<UninitializedPropertyAccessException>("lateinit property baseUrl has not been initialized") {
            Secrets.redisUrl
        }
    }


}