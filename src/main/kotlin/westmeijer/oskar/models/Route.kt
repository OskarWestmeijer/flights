package westmeijer.oskar.models

import kotlinx.serialization.Serializable

@Serializable
data class Route(
        val id: Int,
        val from: Airport,
        val to: Airport)
