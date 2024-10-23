package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.AccountEntity
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DefaultAccountRepository(
    private val accountDao: AccountDao,
) : AccountRepository {
    override suspend fun getAll(): List<AccountModel> = accountDao.getAll().map { it.toModel() }

    override fun getAllAsFlow(): Flow<List<AccountModel>> =
        accountDao.getAllAsFlow().map { list ->
            list.map { it.toModel() }
        }

    override suspend fun getBy(id: Long): AccountModel? = accountDao.getBy(id)?.toModel()

    override suspend fun getBy(handle: String): AccountModel? = accountDao.getBy(handle)?.toModel()

    override suspend fun getActive(): AccountModel? = accountDao.getActive()?.toModel()

    override fun getActiveAsFlow(): Flow<AccountModel?> =
        accountDao.getActiveAsFlow().map { list ->
            list.firstOrNull()?.toModel()
        }

    override suspend fun create(account: AccountModel) {
        accountDao.insert(account.toEntity())
    }

    override suspend fun setActive(
        account: AccountModel,
        active: Boolean,
    ) {
        val old = accountDao.getActive()
        if (old != null && active) {
            accountDao.replaceActive(
                old = old.copy(active = false),
                new = account.copy(active = true).toEntity(),
            )
        } else {
            accountDao.update(account.copy(active = active).toEntity())
        }
    }

    override suspend fun update(account: AccountModel) {
        accountDao.update(account.toEntity())
    }

    override suspend fun delete(account: AccountModel) {
        accountDao.delete(account.toEntity())
    }
}

private fun AccountModel.toEntity() =
    AccountEntity(
        id = id,
        handle = handle,
        active = active,
        remoteId = remoteId,
        avatar = avatar,
        displayName = displayName,
        pushAuth = pushAuth,
        pushServerKey = pushServerKey,
        pushPubKey = pushPubKey,
        pushPrivKey = pushPrivKey,
        unifiedPushUrl = unifiedPushUrl,
    )

private fun AccountEntity.toModel() =
    AccountModel(
        id = id,
        handle = handle,
        active = active,
        remoteId = remoteId,
        avatar = avatar,
        displayName = displayName,
        pushAuth = pushAuth,
        pushServerKey = pushServerKey,
        pushPubKey = pushPubKey,
        pushPrivKey = pushPrivKey,
        unifiedPushUrl = unifiedPushUrl,
    )
