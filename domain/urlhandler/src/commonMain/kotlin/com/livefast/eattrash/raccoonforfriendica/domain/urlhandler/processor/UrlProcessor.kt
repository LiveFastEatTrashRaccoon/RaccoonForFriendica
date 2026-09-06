package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

interface UrlProcessor {
    suspend fun process(uri: String): Boolean
}
