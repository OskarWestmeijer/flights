import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import westmeijer.oskar.models.Airport
import westmeijer.oskar.models.FlightRoute
import westmeijer.oskar.redis.FlightRoutesCacheCodec
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class FlightRoutesCacheCodecTest {

    private val codec = FlightRoutesCacheCodec()

    @Test
    fun testDecodeKey() {
        val key = "testKey"
        val encodedKey = codec.encodeKey(key)

        assertEquals(key, codec.decodeKey(encodedKey))
    }

    @Test
    fun testDecodeValueWithValidJson() {
        val airport = Airport("HEL", "60.3172", "24.9633")
        val flightRoutes = listOf(FlightRoute(airport, airport, 5))

        val encodedValue = codec.encodeValue(flightRoutes)
        val decodedValue = codec.decodeValue(encodedValue!!)

        assertEquals(flightRoutes, decodedValue)
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
        val airport = Airport("HEL", "60.3172", "24.9633")
        val flightRoutes = listOf(FlightRoute(airport, airport, 5))

        val encodedValue = codec.encodeValue(flightRoutes)
        val jsonString = Json.encodeToString(ListSerializer(FlightRoute.serializer()), flightRoutes)

        assertEquals(jsonString, encodedValue?.let { String(it.array()) })
    }
}
