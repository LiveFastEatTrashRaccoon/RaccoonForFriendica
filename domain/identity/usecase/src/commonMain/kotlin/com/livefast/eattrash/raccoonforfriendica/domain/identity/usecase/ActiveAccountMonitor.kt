package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

interface ActiveAccountMonitor {
    fun start()

    suspend fun isNotLoggedButItShould(): Boolean

    suspend fun forceRefresh()
}
