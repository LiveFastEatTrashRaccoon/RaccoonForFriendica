package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface CirclesRepository {
    suspend fun getAll(): List<CircleModel>?

    suspend fun get(id: String): CircleModel?

    suspend fun getMembers(
        id: String,
        pageCursor: String? = null,
    ): List<UserModel>?

    suspend fun create(
        title: String,
        replyPolicy: CircleReplyPolicy = CircleReplyPolicy.List,
        exclusive: Boolean = false,
    ): CircleModel?

    suspend fun update(
        id: String,
        title: String,
        replyPolicy: CircleReplyPolicy = CircleReplyPolicy.List,
        exclusive: Boolean = false,
    ): CircleModel?

    suspend fun delete(id: String): Boolean

    suspend fun addMembers(
        id: String,
        userIds: List<String>,
    ): Boolean

    suspend fun removeMembers(
        id: String,
        userIds: List<String>,
    ): Boolean
}
