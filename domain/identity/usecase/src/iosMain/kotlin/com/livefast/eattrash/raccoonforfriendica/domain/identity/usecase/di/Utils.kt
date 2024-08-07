package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getSetupAccountUseCase(): SetupAccountUseCase = DomainIdentityUseCaseDiHelper.setupAccountUseCase

internal object DomainIdentityUseCaseDiHelper : KoinComponent {
    val setupAccountUseCase: SetupAccountUseCase by inject()
}
