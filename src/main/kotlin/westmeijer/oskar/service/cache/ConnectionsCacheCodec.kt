package westmeijer.oskar.service.cache

import io.lettuce.core.codec.RedisCodec
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import westmeijer.oskar.service.connections.model.Connection
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.Charset


internal class ConnectionsCacheCodec : RedisCodec<String, List<Connection>> {
    private val charset: Charset = Charset.forName("UTF-8")

    override fun decodeKey(bytes: ByteBuffer): String {
        return charset.decode(bytes).toString()
    }

    override fun decodeValue(bytes: ByteBuffer): List<Connection>? {
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

    override fun encodeValue(value: List<Connection>): ByteBuffer? {
        try {

            val jsonString = Json.encodeToString(ListSerializer(Connection.serializer()), value)
            return ByteBuffer.wrap(jsonString.toByteArray(charset))
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}