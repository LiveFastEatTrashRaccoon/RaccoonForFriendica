package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.UserRateLimitDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.UserRateLimitEntity
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserRateLimitModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultUserRateLimitRepository(
    private val userRateLimitDao: UserRateLimitDao,
) : UserRateLimitRepository {
    override suspend fun getAll(accountId: Long): List<UserRateLimitModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                userRateLimitDao.getAll(accountId).map { it.toModel() }
            }.getOrElse { emptyList() }
        }

    override suspend fun getBy(
        handle: String,
        accountId: Long,
    ): UserRateLimitModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                userRateLimitDao
                    .getBy(
                        accountId = accountId,
                        handle = handle,
                    )?.toModel()
            }.getOrNull()
        }

    override suspend fun create(model: UserRateLimitModel): UserRateLimitModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                userRateLimitDao.insert(model.toEntity())
                getBy(handle = model.handle, accountId = model.accountId)
            }.getOrNull()
        }

    override suspend fun update(model: UserRateLimitModel): UserRateLimitModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                userRateLimitDao.update(model.toEntity())
                getBy(handle = model.handle, accountId = model.accountId)
            }.getOrNull()
        }

    override suspend fun delete(id: Long): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val entity = UserRateLimitEntity(id = id, userHandle = "")
                userRateLimitDao.delete(entity)
                true
            }.getOrElse { false }
        }
}

private fun UserRateLimitEntity.toModel() =
    UserRateLimitModel(
        id = id,
        handle = userHandle,
        rate = rate,
        accountId = accountId,
    )

private fun UserRateLimitModel.toEntity() =
    UserRateLimitEntity(
        id = id,
        userHandle = handle,
        rate = rate,
        accountId = accountId,
    )
