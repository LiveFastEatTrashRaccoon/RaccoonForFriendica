package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName

enum class MediaType {
    @SerialName("image")
    IMAGE,

    @SerialName("video")
    VIDEO,

    @SerialName("gifv")
    GIFV,

    @SerialName("audio")
    AUDIO,

    @SerialName("unknown")
    UNKNOWN,
}
