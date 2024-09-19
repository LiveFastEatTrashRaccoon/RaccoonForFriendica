package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class PreviewCardModel(
    val description: String = "",
    val image: String? = null,
    val providerName: String = "",
    val title: String = "",
    val type: PreviewType = PreviewType.Unknown,
    val url: String = "",
)
