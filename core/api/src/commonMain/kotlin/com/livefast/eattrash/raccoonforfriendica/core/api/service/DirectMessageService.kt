package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPrivateMessage
import io.ktor.client.request.forms.FormDataContent

interface DirectMessageService {
    suspend fun getAll(
        count: Int,
        page: Int,
        maxId: Long? = null,
        getText: String = "html",
    ): List<FriendicaPrivateMessage>

    suspend fun getConversation(
        uri: String,
        count: Int,
        page: Int,
        maxId: Long? = null,
        sinceId: Long? = null,
        getText: String = "html",
    ): List<FriendicaPrivateMessage>

    suspend fun create(data: FormDataContent): FriendicaPrivateMessage

    suspend fun delete(id: Long): FriendicaApiResult

    suspend fun markAsRead(id: Long): FriendicaApiResult
}
