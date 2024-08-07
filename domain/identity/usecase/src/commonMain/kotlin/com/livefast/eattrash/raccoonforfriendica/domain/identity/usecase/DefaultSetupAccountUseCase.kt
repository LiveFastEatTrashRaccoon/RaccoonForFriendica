package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

private const val ANONYMOUS_ACCOUNT_HANDLE = ""

internal class DefaultSetupAccountUseCase(
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
) : SetupAccountUseCase {
    override suspend fun invoke() =
        withContext(Dispatchers.IO) {
            val accounts = accountRepository.getAll()
            if (accounts.isEmpty()) {
                // create at least an anonymous account
                val account =
                    AccountModel(
                        handle = ANONYMOUS_ACCOUNT_HANDLE,
                        active = true,
                    )
                accountRepository.create(account)
            }

            // mark the anon account as active is no one is found
            val oldActive = accountRepository.getActive()
            if (oldActive == null) {
                val account = accountRepository.getBy(handle = ANONYMOUS_ACCOUNT_HANDLE)
                if (account != null) {
                    val newAccount = account.copy(active = true)
                    accountRepository.update(newAccount)
                }
            }

            // initialize the settings and create them if not existing
            val accountId = accountRepository.getActive()?.id ?: 0
            val oldSettings = settingsRepository.get(accountId)
            if (oldSettings == null) {
                val newSettings = SettingsModel(accountId = accountId)
                settingsRepository.create(newSettings)
            }

            val settings = settingsRepository.get(accountId) ?: SettingsModel()
            settingsRepository.changeCurrent(settings)
        }
}
