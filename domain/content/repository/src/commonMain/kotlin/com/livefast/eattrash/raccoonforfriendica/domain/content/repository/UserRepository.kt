package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface UserRepository {
    suspend fun getById(id: String): UserModel?

    suspend fun search(
        query: String,
        offset: Int,
    ): List<UserModel>

    suspend fun getByHandle(handle: String): UserModel?

    suspend fun getRelationships(ids: List<String>): List<RelationshipModel>

    suspend fun getSuggestions(): List<UserModel>

    suspend fun getFollowers(
        id: String,
        pageCursor: String? = null,
    ): List<UserModel>

    suspend fun getFollowing(
        id: String,
        pageCursor: String? = null,
    ): List<UserModel>

    suspend fun follow(
        id: String,
        reblogs: Boolean = true,
        notifications: Boolean = false,
    ): RelationshipModel?

    suspend fun unfollow(id: String): RelationshipModel?

    suspend fun getFollowRequests(pageCursor: String?): List<UserModel>

    suspend fun acceptFollowRequest(id: String): Boolean

    suspend fun rejectFollowRequest(id: String): Boolean
}
