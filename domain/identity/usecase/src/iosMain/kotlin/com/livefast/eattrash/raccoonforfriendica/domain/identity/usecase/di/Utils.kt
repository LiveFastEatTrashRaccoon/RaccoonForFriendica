package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.OpenUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

actual fun getSetupAccountUseCase(): SetupAccountUseCase = DomainIdentityUseCaseDiHelper.setupAccountUseCase

actual fun getOpenUrlUseCase(default: UriHandler): OpenUrlUseCase = DomainIdentityUseCaseDiHelper.getOpenUrlUseCase(default)

internal object DomainIdentityUseCaseDiHelper : KoinComponent {
    val setupAccountUseCase: SetupAccountUseCase by inject()

    fun getOpenUrlUseCase(default: UriHandler): OpenUrlUseCase {
        val res by inject<OpenUrlUseCase>(
            parameters = {
                parametersOf(default)
            },
        )
        return res
    }
}
