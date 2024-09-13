package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface UserRepository {
    suspend fun getById(id: String): UserModel?

    suspend fun search(
        query: String,
        offset: Int,
    ): List<UserModel>?

    suspend fun getByHandle(handle: String): UserModel?

    suspend fun getCurrent(): UserModel?

    suspend fun getRelationships(ids: List<String>): List<RelationshipModel>?

    suspend fun getSuggestions(): List<UserModel>?

    suspend fun getFollowers(
        id: String,
        pageCursor: String? = null,
    ): List<UserModel>?

    suspend fun getFollowing(
        id: String,
        pageCursor: String? = null,
    ): List<UserModel>?

    suspend fun searchMyFollowing(
        query: String,
        pageCursor: String? = null,
    ): List<UserModel>?

    suspend fun follow(
        id: String,
        reblogs: Boolean = true,
        notifications: Boolean = false,
    ): RelationshipModel?

    suspend fun unfollow(id: String): RelationshipModel?

    suspend fun getFollowRequests(pageCursor: String? = null): Pair<List<UserModel>, String>?

    suspend fun acceptFollowRequest(id: String): Boolean

    suspend fun rejectFollowRequest(id: String): Boolean

    suspend fun mute(
        id: String,
        durationSeconds: Long = 0,
        notifications: Boolean = true,
    ): RelationshipModel?

    suspend fun unmute(id: String): RelationshipModel?

    suspend fun block(id: String): RelationshipModel?

    suspend fun unblock(id: String): RelationshipModel?

    suspend fun getMuted(pageCursor: String? = null): List<UserModel>?

    suspend fun getBlocked(pageCursor: String? = null): List<UserModel>?

    suspend fun updateProfile(
        note: String? = null,
        displayName: String? = null,
        avatar: ByteArray? = null,
        header: ByteArray? = null,
        locked: Boolean? = null,
        bot: Boolean? = null,
        discoverable: Boolean? = null,
        hideCollections: Boolean? = null,
        indexable: Boolean? = null,
        fields: Map<String, String>? = null,
    ): UserModel?

    suspend fun updatePersonalNote(
        id: String,
        value: String,
    ): RelationshipModel?
}
