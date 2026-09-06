package com.livefast.eattrash.raccoonforfriendica.core.api.form

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCollectionForm(
    @SerialName("account_ids") val accountIds: List<String>? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("discoverable") val discoverable: Boolean = true,
    @SerialName("language") val language: String? = null,
    @SerialName("name") val name: String = "",
    @SerialName("sensitive") val sensitive: Boolean = false,
    @SerialName("tag_name") val tagName: String? = null,
)
