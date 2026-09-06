package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Collection(
    @SerialName("account_id") val accountId: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("description") val description: String,
    @SerialName("discoverable") val discoverable: Boolean = true,
    @SerialName("id") val id: String,
    @SerialName("item_count") val itemCount: Int = 0,
    @SerialName("items") val items: List<CollectionItem> = emptyList(),
    @SerialName("language") val language: String,
    @SerialName("local") val local: Boolean = true,
    @SerialName("name") val name: String,
    @SerialName("sensitive") val sensitive: Boolean = false,
    @SerialName("tag") val tag: Tag? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("uri") val uri: String,
    @SerialName("url") val url: String,
)

data class Collections(@SerialName("collections") val collections: List<Collection> = emptyList())
