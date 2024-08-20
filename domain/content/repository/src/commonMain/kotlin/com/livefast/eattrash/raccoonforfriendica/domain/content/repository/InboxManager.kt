package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import kotlinx.coroutines.flow.StateFlow

interface InboxManager {
    val unreadCount: StateFlow<Int>

    suspend fun refreshUnreadCount()

    suspend fun decrementUnreadCount()
}
