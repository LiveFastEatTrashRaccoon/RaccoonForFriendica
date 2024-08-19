package com.livefast.eattrash.raccoonforfriendica.core.api.provider

sealed interface ServiceCredentials {
    data class Basic(
        val user: String,
        val pass: String,
    ) : ServiceCredentials

    data class OAuth2(
        val accessToken: String,
        val refreshToken: String,
    ) : ServiceCredentials
}
