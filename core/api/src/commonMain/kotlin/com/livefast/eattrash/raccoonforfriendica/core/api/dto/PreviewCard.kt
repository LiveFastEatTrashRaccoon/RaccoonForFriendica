package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreviewCard(
    @SerialName("url") val url: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("image") val image: String? = null,
    @SerialName("provider_name") val providerName: String = "",
    @SerialName("type") val type: PreviewCardType = PreviewCardType.PHOTO,
)
