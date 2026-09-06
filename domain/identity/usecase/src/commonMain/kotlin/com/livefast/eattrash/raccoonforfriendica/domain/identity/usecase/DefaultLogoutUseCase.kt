package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultLogoutUseCase(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
) : LogoutUseCase {
    override suspend fun invoke() {
        apiConfigurationRepository.setAuth(null)
        val activeAccount = accountRepository.getActive()
        if (activeAccount != null) {
            val anonymousAccount = accountRepository.getBy(handle = "")
            if (anonymousAccount != null) {
                accountRepository.setActive(anonymousAccount, true)
            } else {
                accountRepository.setActive(activeAccount, false)
            }
        }
    }
}
