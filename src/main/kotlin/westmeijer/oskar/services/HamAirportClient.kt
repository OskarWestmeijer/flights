package westmeijer.oskar.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import westmeijer.oskar.Secrets
import westmeijer.oskar.models.Airport
import westmeijer.oskar.models.Destination
import westmeijer.oskar.models.FlightRoute
import java.time.Instant
import java.time.temporal.ChronoUnit

private val ham = Airport("HAM", "", "")

object HamAirportClient {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

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

        val destination = response.body<List<Destination>>()
        val flightRoutes =
            destination.map { FlightRoute(1, ham, AirportService.getAirport(it.destinationAirport3LCode)) }
        return flightRoutes
    }

}
