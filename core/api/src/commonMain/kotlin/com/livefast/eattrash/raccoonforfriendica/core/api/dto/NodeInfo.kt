package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NodeInfo(@SerialName("links") val links: List<NodeInfoLink> = emptyList())

@Serializable
data class NodeInfoLink(@SerialName("rel") val rel: String? = null, @SerialName("href") val href: String? = null)
