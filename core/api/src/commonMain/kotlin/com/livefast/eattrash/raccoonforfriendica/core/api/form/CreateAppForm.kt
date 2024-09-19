package com.livefast.eattrash.raccoonforfriendica.core.api.form

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAppForm(
    @SerialName("client_name") val clientName: String = "",
    @SerialName("redirect_uris") val redirectUris: String = "",
    @SerialName("scopes") val scopes: String = "",
    @SerialName("website") val website: String = "",
)
