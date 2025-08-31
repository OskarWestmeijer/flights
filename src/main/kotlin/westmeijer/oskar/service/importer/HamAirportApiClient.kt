package westmeijer.oskar.service.importer

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import westmeijer.oskar.Secrets
import westmeijer.oskar.service.importer.model.ArrivingFlight
import westmeijer.oskar.service.importer.model.DepartingFlight
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

    install(HttpTimeout) {
        requestTimeoutMillis = 30_000 // max total request time
        connectTimeoutMillis = 10_000 // time to establish TCP connection
        socketTimeoutMillis = 30_000  // time between packets (read/write)
    }
}

internal object HamAirportApiClient {

    suspend fun getDepartingFlights(): List<DepartingFlight> {
        val today = Instant.now().truncatedTo(ChronoUnit.DAYS)
        val tomorrow = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS)

        val response: HttpResponse = client.get("${Secrets.baseUrl}/v2/flights/departures?from=${today}&to=${tomorrow}") {
            headers {
                append("Ocp-Apim-Subscription-Key", Secrets.apiKey)
                append("Content-Type", "application/json")
                append("Accept", "application/json")
            }
        }

        val destinationAsText = response.bodyAsText().trimIndent()
        return decoder.decodeFromString<List<DepartingFlight>>(destinationAsText)
    }

    suspend fun getArrivingFlights(): List<ArrivingFlight> {
        val today = Instant.now().truncatedTo(ChronoUnit.DAYS)
        val tomorrow = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS)

        val response: HttpResponse = client.get("${Secrets.baseUrl}/v2/flights/arrivals?from=${today}&to=${tomorrow}") {
            headers {
                append("Ocp-Apim-Subscription-Key", Secrets.apiKey)
                append("Content-Type", "application/json")
                append("Accept", "application/json")
            }
        }

        val destinationAsText = response.bodyAsText().trimIndent()
        return decoder.decodeFromString<List<ArrivingFlight>>(destinationAsText)
    }

}
