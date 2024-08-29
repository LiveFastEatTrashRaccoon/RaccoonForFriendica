package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun getAll(): List<AccountModel>

    fun getAllAsFlow(): Flow<List<AccountModel>>

    suspend fun getBy(handle: String): AccountModel?

    suspend fun getActive(): AccountModel?

    fun getActiveAsFlow(): Flow<AccountModel?>

    suspend fun create(account: AccountModel)

    suspend fun setActive(
        account: AccountModel,
        active: Boolean,
    )

    suspend fun delete(account: AccountModel)
}
