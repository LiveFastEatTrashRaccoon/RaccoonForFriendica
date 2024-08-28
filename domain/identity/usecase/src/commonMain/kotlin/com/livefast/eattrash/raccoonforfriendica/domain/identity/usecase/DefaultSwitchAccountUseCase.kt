package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountCredentialsCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository

internal class DefaultSwitchAccountUseCase(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
    private val identityRepository: IdentityRepository,
    private val accountCredentialsCache: AccountCredentialsCache,
) : SwitchAccountUseCase {
    override suspend fun invoke(account: AccountModel) {
        val oldActive = accountRepository.getActive()
        if (oldActive != null) {
            accountRepository.update(oldActive.copy(active = false))
        }

        val newAccount = account.copy(active = true)
        accountRepository.update(newAccount)

        val accountId = account.id
        val anonymousAccountId = accountRepository.getBy("")?.id ?: 0
        val defaultSettings = settingsRepository.get(anonymousAccountId) ?: SettingsModel()
        val settings = settingsRepository.get(accountId) ?: defaultSettings
        settingsRepository.changeCurrent(settings)

        val credentials = accountCredentialsCache.get(accountId)
        val node = account.handle.substringAfter("@")
        apiConfigurationRepository.changeNode(node)
        apiConfigurationRepository.setAuth(credentials)

        identityRepository.refreshCurrentUser()
    }
}
