package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

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

    override suspend fun getRelationship(id: String): RelationshipModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.users
                    .getRelationships(id)
                    .firstOrNull()
                    ?.toModel()
            }
        }.getOrNull()

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

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
