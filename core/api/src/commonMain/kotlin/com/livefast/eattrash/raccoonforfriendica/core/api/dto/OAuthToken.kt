package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OAuthToken(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
)

private val JsonSerializer = Json { ignoreUnknownKeys = true }

fun String.toOauthToken() = JsonSerializer.decodeFromString<OAuthToken>(this)
