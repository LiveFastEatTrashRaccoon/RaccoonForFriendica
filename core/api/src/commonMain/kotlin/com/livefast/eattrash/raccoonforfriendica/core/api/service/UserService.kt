package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Suggestion
import com.livefast.eattrash.raccoonforfriendica.core.api.form.FollowUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.MuteUserForm
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent

interface UserService {
    suspend fun verifyCredentials(): CredentialAccount

    suspend fun getById(id: String): Account

    suspend fun search(
        query: String = "",
        offset: Int = 0,
        resolve: Boolean = false,
        following: Boolean = false,
    ): List<Account>

    suspend fun getStatuses(
        id: String,
        maxId: String? = null,
        minId: String? = null,
        onlyMedia: Boolean = false,
        excludeReplies: Boolean = false,
        excludeReblogs: Boolean = false,
        pinned: Boolean = false,
        limit: Int = 20,
    ): List<Status>

    suspend fun getRelationships(id: List<String>): List<Relationship>

    suspend fun getSuggestions(limit: Int): List<Suggestion>

    suspend fun getFollowers(id: String, maxId: String? = null, minId: String? = null, limit: Int = 20): List<Account>

    suspend fun getFollowing(id: String, maxId: String? = null, minId: String? = null, limit: Int = 20): List<Account>

    suspend fun follow(id: String, data: FollowUserForm): Relationship

    suspend fun unfollow(id: String): Relationship

    suspend fun getFavorites(maxId: String? = null, minId: String? = null, limit: Int = 20): List<Status>

    suspend fun getBookmarks(maxId: String? = null, minId: String? = null, limit: Int = 20): List<Status>

    suspend fun mute(id: String, data: MuteUserForm): Relationship

    suspend fun unmute(id: String): Relationship

    suspend fun block(id: String): Relationship

    suspend fun unblock(id: String): Relationship

    suspend fun getMuted(maxId: String? = null, limit: Int = 20): List<Account>

    suspend fun getBlocked(maxId: String? = null, limit: Int = 20): List<Account>

    suspend fun updateProfile(content: FormDataContent): Account

    suspend fun updateProfileImage(content: MultiPartFormDataContent): Account

    suspend fun updatePersonalNote(id: String, data: FormDataContent): Relationship
}
