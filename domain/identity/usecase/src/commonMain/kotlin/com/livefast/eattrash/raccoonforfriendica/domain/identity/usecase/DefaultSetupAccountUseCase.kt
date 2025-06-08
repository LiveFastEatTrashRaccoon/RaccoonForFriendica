package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultSetupAccountUseCase(
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
) : SetupAccountUseCase {
    override suspend fun invoke() = withContext(Dispatchers.IO) {
        val accounts = accountRepository.getAll()
        if (accounts.isEmpty()) {
            // create at least an anonymous account
            val account = AccountModel(handle = "")
            accountRepository.create(account)

            // initialize anonymous account settings
            val anonymousAccount = accountRepository.getBy(handle = "")
            if (anonymousAccount != null) {
                val oldSettings = settingsRepository.get(anonymousAccount.id)
                if (oldSettings == null) {
                    val newSettings = SettingsModel(accountId = anonymousAccount.id)
                    settingsRepository.create(newSettings)
                }

                accountRepository.setActive(anonymousAccount, true)
            }
        }
    }
}
