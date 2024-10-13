package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

interface ContentPreloadManager {
    suspend fun preload(userRemoteId: String? = null)
}
