package westmeijer.oskar.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.logging.*
import kotlinx.serialization.json.Json
import westmeijer.oskar.Secrets
import westmeijer.oskar.models.Airport
import westmeijer.oskar.models.Destination
import westmeijer.oskar.models.FlightRoute
import java.time.Instant
import java.time.temporal.ChronoUnit


internal val log = KtorSimpleLogger("westmeijer.oskar.services.HamAirportClient")

val decoder = Json {
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

private val hamAirport = Airport("HAM", "53.6304", "9.98823")

object HamAirportClient {
    suspend fun requestApi(): List<FlightRoute> {
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
        val destinationList = decoder.decodeFromString<List<Destination>>(destinationAsText)
        log.info("Received destination count: ${destinationList.size}")

        val flightRoutes =
            destinationList
                .mapNotNull { AirportService.getAirport(it.destinationAirport3LCode) }
                .map {
                    FlightRoute(1, hamAirport, it)
                }

        log.info("Mapped to flightRoutes count: ${flightRoutes.size}")
        return flightRoutes
    }

}
