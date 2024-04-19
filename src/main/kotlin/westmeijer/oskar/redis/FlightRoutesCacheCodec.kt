package westmeijer.oskar.redis

import io.lettuce.core.codec.RedisCodec
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import westmeijer.oskar.models.server.FlightRoute
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.Charset


internal class FlightRoutesCacheCodec : RedisCodec<String, List<FlightRoute>> {
    private val charset: Charset = Charset.forName("UTF-8")

    override fun decodeKey(bytes: ByteBuffer): String {
        return charset.decode(bytes).toString()
    }

    override fun decodeValue(bytes: ByteBuffer): List<FlightRoute>? {
        try {
            val jsonString = charset.decode(bytes).toString()
            return Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            return null
        }
    }

    override fun encodeKey(key: String): ByteBuffer {
        return charset.encode(key)
    }

    override fun encodeValue(value: List<FlightRoute>): ByteBuffer? {
        try {

            val jsonString = Json.encodeToString(ListSerializer(FlightRoute.serializer()), value)
            return ByteBuffer.wrap(jsonString.toByteArray(charset))
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}