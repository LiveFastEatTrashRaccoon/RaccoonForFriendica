package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository

internal class DefaultSwitchAccountUseCase(private val accountRepository: AccountRepository) : SwitchAccountUseCase {
    override suspend fun invoke(account: AccountModel) {
        accountRepository.setActive(account, true)
    }
}
