package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class PreviewCardModel(
    val type: PreviewType = PreviewType.Unknown,
    val url: String = "",
    val image: String? = null,
    val title: String = "",
    val description: String = "",
    val providerName: String = "",
)
