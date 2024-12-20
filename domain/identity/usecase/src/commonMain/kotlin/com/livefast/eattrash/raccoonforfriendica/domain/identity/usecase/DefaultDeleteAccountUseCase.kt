package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountCredentialsCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository

internal class DefaultDeleteAccountUseCase(
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
    private val accountCredentialsCache: AccountCredentialsCache,
) : DeleteAccountUseCase {
    override suspend fun invoke(account: AccountModel) {
        val activeId = accountRepository.getActive()?.id
        val accountId = account.id
        check(activeId != accountId) { "The active account can not be deleted" }

        accountCredentialsCache.remove(accountId)
        val settings = settingsRepository.get(accountId)
        if (settings != null) {
            settingsRepository.delete(settings)
        }
        accountRepository.delete(account)
    }
}
