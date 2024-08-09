package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipModel
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

    override suspend fun getByHandle(handle: String): AccountModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.accounts.lookup(handle).toModel()
            }
        }.getOrNull()

    override suspend fun getRelationship(id: String): RelationshipModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.accounts
                    .getRelationships(id)
                    .firstOrNull()
                    ?.toModel()
            }
        }.getOrNull()

    override suspend fun getSuggestions(): List<AccountModel> =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.accounts
                    .getSuggestions(
                        limit = DEFAULT_PAGE_SIZE,
                    ).map { it.account.toModel() }
            }
        }.apply {
            exceptionOrNull()?.also {
                it.printStackTrace()
            }
        }.getOrElse { emptyList() }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 40
    }
}
