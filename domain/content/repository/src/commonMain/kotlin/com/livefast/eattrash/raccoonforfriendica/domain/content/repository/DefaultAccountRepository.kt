package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultAccountRepository(
    private val provider: ServiceProvider,
) : AccountRepository {
    override suspend fun getById(id: String): AccountModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.accounts.getById(id).toModel()
            }
        }.getOrNull()
}
