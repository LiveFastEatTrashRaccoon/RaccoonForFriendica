package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor

internal interface UrlProcessor {
    suspend fun process(uri: String): Boolean
}
