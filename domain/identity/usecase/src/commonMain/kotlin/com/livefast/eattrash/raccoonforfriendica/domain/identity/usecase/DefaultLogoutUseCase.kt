package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository

internal class DefaultLogoutUseCase(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
    private val identityRepository: IdentityRepository,
) : LogoutUseCase {
    override suspend fun invoke() {
        apiConfigurationRepository.setAuth(null)

        val activeAccount = accountRepository.getActive()
        if (activeAccount != null) {
            val newAccount = activeAccount.copy(active = false)
            accountRepository.update(newAccount)

            val anonymousAccountId = accountRepository.getBy("")?.id ?: 0
            val defaultSettings = settingsRepository.get(anonymousAccountId) ?: SettingsModel()
            settingsRepository.changeCurrent(defaultSettings)

            identityRepository.refreshCurrentUser()
        }
    }
}
