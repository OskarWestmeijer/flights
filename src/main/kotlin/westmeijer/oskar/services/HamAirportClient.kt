package westmeijer.oskar.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import westmeijer.oskar.Secrets
import westmeijer.oskar.models.Destination
import java.time.Instant
import java.time.temporal.ChronoUnit


private val decoder = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

/**
 * The json decoding from body is not used. The api response did not specify a Content-Type, which caused an exception on each request.
 * @see NoTransformationFoundException
 */
private val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }, ContentType.Any)
    }
}

object HamAirportClient {

    suspend fun getDestinations(): List<Destination> {
        val today = Instant.now().truncatedTo(ChronoUnit.DAYS)
        val tomorrow = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS)

        val response: HttpResponse =
            client
                .get("${Secrets.baseUrl}/v2/flights/departures?from=${today}&to=${tomorrow}") {
                    headers {
                        append("Ocp-Apim-Subscription-Key", Secrets.apiKey)
                        append("Content-Type", "application/json")
                        append("Accept", "application/json")
                    }
                }

        val destinationAsText = response.bodyAsText().trimIndent()
        return decoder.decodeFromString<List<Destination>>(destinationAsText)
    }

}
