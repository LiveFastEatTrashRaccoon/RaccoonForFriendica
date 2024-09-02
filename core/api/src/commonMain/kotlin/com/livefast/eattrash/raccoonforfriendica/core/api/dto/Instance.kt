package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Instance(
    @SerialName("domain") val domain: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("version") val version: String? = null,
    @SerialName("source_url") val sourceUrl: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("usage") val usage: InstanceUsage? = null,
    @SerialName("thumbnail") val thumbnail: InstanceThumbnail? = null,
    @SerialName("icon") val icon: List<InstanceIcon> = emptyList(),
    @SerialName("languages") val languages: List<String> = emptyList(),
    @SerialName("rules") val rules: List<InstanceRule> = emptyList(),
    @SerialName("contact_account") val contactAccount: Account? = null,
)
