package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

internal interface UrlProcessor {
    suspend fun process(uri: String): Boolean
}
