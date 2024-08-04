package com.github.akesiseli.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName

enum class ContentVisibility {
    @SerialName("public")
    PUBLIC,

    @SerialName("unlisted")
    UNLISTED,

    @SerialName("private")
    PRIVATE,

    @SerialName("direct")
    DIRECT,
}
