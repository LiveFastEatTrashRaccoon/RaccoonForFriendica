package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName

enum class PreviewCardType {
    @SerialName("link")
    LINK,

    @SerialName(
        "photo",
    )
    PHOTO,

    @SerialName(
        "video",
    )
    VIDEO,

    @SerialName("rich")
    RICH,
}
