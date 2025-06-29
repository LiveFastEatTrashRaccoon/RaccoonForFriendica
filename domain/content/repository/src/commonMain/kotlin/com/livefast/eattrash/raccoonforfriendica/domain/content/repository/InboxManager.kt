package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import kotlinx.coroutines.flow.StateFlow

interface InboxManager {
    val unreadCount: StateFlow<Int>

    suspend fun clearUnreadCount()

    suspend fun refreshUnreadCount()

    suspend fun decrementUnreadCount()

    companion object {
        const val OPEN_INBOX_AT_STARTUP = "openInboxAtStartup"
    }
}
