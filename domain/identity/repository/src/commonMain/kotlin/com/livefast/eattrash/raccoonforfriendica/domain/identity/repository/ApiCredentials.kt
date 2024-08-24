package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCredentials

sealed interface ApiCredentials {
    data class HttpBasic(
        val user: String,
        val pass: String,
    ) : ApiCredentials

    data class OAuth2(
        val accessToken: String,
        val refreshToken: String,
    ) : ApiCredentials
}

internal fun ApiCredentials.toServiceCredentials() =
    when (this) {
        is ApiCredentials.HttpBasic ->
            ServiceCredentials.Basic(
                user = user,
                pass = pass,
            )

        is ApiCredentials.OAuth2 ->
            ServiceCredentials.OAuth2(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
    }
