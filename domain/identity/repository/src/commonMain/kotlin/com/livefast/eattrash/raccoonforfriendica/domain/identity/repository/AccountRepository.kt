package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun getAll(): List<AccountModel>

    suspend fun getBy(handle: String): AccountModel?

    suspend fun getActive(): AccountModel?

    suspend fun getActiveAsFlow(): Flow<AccountModel?>

    suspend fun create(account: AccountModel)

    suspend fun update(account: AccountModel)

    suspend fun delete(account: AccountModel)
}
