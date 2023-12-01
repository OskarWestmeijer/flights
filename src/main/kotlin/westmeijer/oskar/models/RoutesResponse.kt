package westmeijer.oskar.models

import kotlinx.serialization.Serializable

@Serializable
data class RoutesResponse(
        val routes: List<Route>)
