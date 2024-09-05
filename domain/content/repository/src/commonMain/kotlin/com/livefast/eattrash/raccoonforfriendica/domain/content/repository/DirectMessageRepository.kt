package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel

interface DirectMessageRepository {
    suspend fun getAll(
        page: Int = 0,
        limit: Int? = null,
    ): List<DirectMessageModel>

    suspend fun getReplies(
        parentUri: String,
        page: Int = 0,
    ): List<DirectMessageModel>

    suspend fun pollReplies(
        parentUri: String,
        minId: String,
    ): List<DirectMessageModel>

    suspend fun create(
        recipientId: String,
        text: String,
        title: String? = null,
        inReplyTo: String? = null,
    ): DirectMessageModel?

    suspend fun delete(id: String): Boolean

    suspend fun markAsRead(id: String): Boolean
}
