package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.form.FollowUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultUserRepository(
    private val provider: ServiceProvider,
) : UserRepository {
    override suspend fun getById(id: String): UserModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users.getById(id).toModel()
            }
        }.getOrNull()

    override suspend fun getByHandle(handle: String): UserModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users.lookup(handle).toModel()
            }
        }.getOrNull()

    override suspend fun getRelationships(ids: List<String>): List<RelationshipModel> =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users
                    .getRelationships(ids)
                    .map { it.toModel() }
            }
        }.getOrElse { emptyList() }

    override suspend fun getSuggestions(): List<UserModel> =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users
                    .getSuggestions(
                        limit = DEFAULT_PAGE_SIZE,
                    ).map { it.user.toModel() }
            }
        }.apply {
            exceptionOrNull()?.also {
                it.printStackTrace()
            }
        }.getOrElse { emptyList() }

    override suspend fun getFollowers(
        id: String,
        pageCursor: String?,
    ): List<UserModel> =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users
                    .getFollowers(
                        id = id,
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                    ).map { it.toModel() }
            }
        }.getOrElse { emptyList() }

    override suspend fun getFollowing(
        id: String,
        pageCursor: String?,
    ): List<UserModel> =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users
                    .getFollowing(
                        id = id,
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                    ).map { it.toModel() }
            }
        }.getOrElse { emptyList() }

    override suspend fun follow(
        id: String,
        reblogs: Boolean,
        notify: Boolean,
    ): RelationshipModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                val data = FollowUserForm(reblogs = reblogs, notify = notify)
                provider.users
                    .follow(
                        id = id,
                        data = data,
                    ).toModel()
            }
        }.getOrNull()

    override suspend fun unfollow(id: String): RelationshipModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users.unfollow(id).toModel()
            }
        }.getOrNull()

    override suspend fun getFollowRequests(pageCursor: String?): List<UserModel> =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users
                    .getFollowRequests(
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                    ).map { it.toModel() }
            }
        }.getOrElse { emptyList() }

    override suspend fun acceptFollowRequest(id: String) =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users.acceptFollowRequest(id)
                true
            }
        }.getOrElse { false }

    override suspend fun rejectFollowRequest(id: String) =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users.rejectFollowRequest(id)
                true
            }
        }.getOrElse { false }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
