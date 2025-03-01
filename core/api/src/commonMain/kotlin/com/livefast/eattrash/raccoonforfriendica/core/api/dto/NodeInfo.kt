package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject

@Serializable
data class NodeInfo(
    @SerialName("links") val links: List<NodeInfoLink> = emptyList(),
)

@Serializable
data class NodeInfoLink(
    @SerialName("rel") val rel: String? = null,
    @SerialName("href") val href: String? = null,
)

object NodeInfoUtils {
    fun linksFromJson(value: String): NodeInfo = JsonSerializer.decodeFromString<NodeInfo>(value)

    fun dataFromJson(value: String): Map<String, Any?> = JsonSerializer.parseToJsonElement(value).jsonObject.toMap()
}
