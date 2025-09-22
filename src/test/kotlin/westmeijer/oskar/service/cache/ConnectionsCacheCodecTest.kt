package westmeijer.oskar.service.cache
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import westmeijer.oskar.service.airport.model.Airport
import westmeijer.oskar.service.connections.model.Connection
import java.nio.ByteBuffer
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ConnectionsCacheCodecTest {

    private val codec = ConnectionsCacheCodec()

    @Test
    fun testDecodeKey() {
        val key = "testKey"
        val encodedKey = codec.encodeKey(key)

        assertEquals(key, codec.decodeKey(encodedKey))
    }

    @Test
    fun testDecodeValueWithValidJson() {
        val airport = Airport("HEL", "Hamburg Airport", "DE", "60.3172", "24.9633")
        val connections = listOf(Connection(
            airport, airport, 1, 2, 3, Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(),
            listOf()
        ))

        val encodedValue = codec.encodeValue(connections)
        val decodedValue = codec.decodeValue(encodedValue!!)

        assertEquals(connections, decodedValue)
    }

    @Test
    fun testDecodeValueWithInvalidJson() {
        val invalidJsonString = "{invalid: json}"

        val encodedValue = ByteBuffer.wrap(invalidJsonString.toByteArray())
        val decodedValue = codec.decodeValue(encodedValue)

        assertNull(decodedValue)
    }

    @Test
    fun testEncodeKey() {
        val key = "testKey"
        val encodedKey = codec.encodeKey(key)

        assertEquals(key, codec.decodeKey(encodedKey))
    }

    @Test
    fun testEncodeValue() {
        val airport = Airport("HEL", "Hamburg Airport", "DE", "60.3172", "24.9633")
        val connections = listOf(Connection(airport, airport, 1, 2,
            3, Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(), listOf()))

        val encodedValue = codec.encodeValue(connections)
        val jsonString = Json.encodeToString(ListSerializer(Connection.serializer()), connections)

        assertEquals(jsonString, encodedValue?.let { String(it.array()) })
    }
}
