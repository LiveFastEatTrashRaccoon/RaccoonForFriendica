package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel

interface SwitchAccountUseCase {
    suspend operator fun invoke(account: AccountModel)
}
