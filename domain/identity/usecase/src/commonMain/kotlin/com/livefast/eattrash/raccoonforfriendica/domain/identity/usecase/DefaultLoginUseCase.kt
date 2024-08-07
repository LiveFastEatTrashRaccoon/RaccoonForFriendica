package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthenticationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository

internal class DefaultLoginUseCase(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
) : LoginUseCase {
    override suspend fun invoke(
        node: String,
        user: String,
        pass: String,
    ) {
        val isValid =
            authenticationRepository.validateCredentials(node = node, user = user, pass = pass)
        check(isValid) { "Invalid credentials" }

        apiConfigurationRepository.changeNode(node)
        apiConfigurationRepository.setAuth(user to pass)

        val handle = "$user@$node".lowercase()
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
                settingsRepository.create(defaultSettings.copy(accountId = accountId))
            }

            val settings = settingsRepository.get(accountId) ?: defaultSettings
            settingsRepository.changeCurrent(settings)
        }
    }
}
