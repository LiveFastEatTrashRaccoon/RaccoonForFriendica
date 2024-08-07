package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toUiTheme
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.AccountEntity
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.SettingsEntity
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DefaultAccountRepository(
    private val accountDao: AccountDao,
) : AccountRepository {
    override suspend fun getAll(): List<AccountModel> = accountDao.getAll().map { it.toModel() }

    override suspend fun getBy(handle: String): AccountModel? = accountDao.getBy(handle)?.toModel()

    override suspend fun getActive(): AccountModel? = accountDao.getActive()?.toModel()

    override suspend fun getActiveAsFlow(): Flow<AccountModel?> =
        accountDao.getActiveAsFlow().map { list ->
            list.firstOrNull()?.toModel()
        }

    override suspend fun create(account: AccountModel) {
        accountDao.insert(account.toEntity())
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
    )

private fun AccountEntity.toModel() =
    AccountModel(
        id = id,
        handle = handle,
        active = active,
    )

private fun SettingsEntity.toModel() =
    SettingsModel(
        lang = lang,
        theme = theme.toUiTheme(),
    )
