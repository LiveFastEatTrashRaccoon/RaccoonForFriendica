package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.UserRateLimitDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.UserRateLimitEntity
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserRateLimitModel
import io.ktor.utils.io.CancellationException

internal class DefaultUserRateLimitRepository(private val userRateLimitDao: UserRateLimitDao) :
    UserRateLimitRepository {
    override suspend fun getAll(accountId: Long): List<UserRateLimitModel> = try {
        userRateLimitDao.getAll(accountId).map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        emptyList()
    }

    override suspend fun getBy(handle: String, accountId: Long): UserRateLimitModel? = try {
        userRateLimitDao
            .getBy(
                accountId = accountId,
                handle = handle,
            )?.toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun create(model: UserRateLimitModel): UserRateLimitModel? = try {
        userRateLimitDao.insert(model.toEntity())
        getBy(handle = model.handle, accountId = model.accountId)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun update(model: UserRateLimitModel): UserRateLimitModel? = try {
        userRateLimitDao.update(model.toEntity())
        getBy(handle = model.handle, accountId = model.accountId)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun delete(id: Long): Boolean = try {
        val entity = UserRateLimitEntity(id = id, userHandle = "")
        userRateLimitDao.delete(entity)
        true
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        false
    }
}

private fun UserRateLimitEntity.toModel() = UserRateLimitModel(
    id = id,
    handle = userHandle,
    rate = rate,
    accountId = accountId,
)

private fun UserRateLimitModel.toEntity() = UserRateLimitEntity(
    id = id,
    userHandle = handle,
    rate = rate,
    accountId = accountId,
)
