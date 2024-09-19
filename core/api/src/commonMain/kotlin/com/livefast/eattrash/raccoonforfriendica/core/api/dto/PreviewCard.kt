package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreviewCard(
    @SerialName("description") val description: String = "",
    @SerialName("image") val image: String? = null,
    @SerialName("provider_name") val providerName: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("type") val type: PreviewCardType = PreviewCardType.PHOTO,
    @SerialName("url") val url: String = "",
)
