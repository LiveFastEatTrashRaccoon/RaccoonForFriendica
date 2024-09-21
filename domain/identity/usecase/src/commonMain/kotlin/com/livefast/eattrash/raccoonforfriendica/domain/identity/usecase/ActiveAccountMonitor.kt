package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import androidx.compose.runtime.Stable

@Stable
interface ActiveAccountMonitor {
    fun start()

    suspend fun isNotLoggedButItShould(): Boolean

    suspend fun forceRefresh()
}
