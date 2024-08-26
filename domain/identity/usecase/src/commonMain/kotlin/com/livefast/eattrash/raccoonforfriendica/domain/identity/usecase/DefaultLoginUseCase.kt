package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiCredentials
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository

internal class DefaultLoginUseCase(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val credentialsRepository: CredentialsRepository,
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
    private val identityRepository: IdentityRepository,
) : LoginUseCase {
    override suspend fun invoke(
        node: String,
        credentials: ApiCredentials,
    ) {
        apiConfigurationRepository.changeNode(node)
        apiConfigurationRepository.setAuth(credentials)
        val user = credentialsRepository.validate(node, credentials)
        checkNotNull(user) { "Invalid credentials" }

        val handle = user.handle ?: user.username ?: ""
        val oldAccount = accountRepository.getBy(handle)
        if (oldAccount == null) {
            accountRepository.create(AccountModel(handle = handle))
        }
        val oldActive = accountRepository.getActive()
        if (oldActive != null) {
            accountRepository.update(oldActive.copy(active = false))
        }
        accountRepository.getBy(handle)?.also {
            val account = it.copy(active = true)
            accountRepository.update(account)

            val accountId = account.id
            val anonymousAccountId = accountRepository.getBy("")?.id ?: 0
            val oldSettings = settingsRepository.get(accountId)
            val defaultSettings = settingsRepository.get(anonymousAccountId) ?: SettingsModel()
            if (oldSettings == null) {
                settingsRepository.create(defaultSettings.copy(id = 0, accountId = accountId))
            }

            val settings = settingsRepository.get(accountId) ?: defaultSettings
            settingsRepository.changeCurrent(settings)

            identityRepository.refreshCurrentUser()
        }
    }
}
