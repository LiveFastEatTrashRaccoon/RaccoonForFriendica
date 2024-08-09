package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.OpenUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent

actual fun getSetupAccountUseCase(): SetupAccountUseCase {
    val res by KoinJavaComponent.inject<SetupAccountUseCase>(SetupAccountUseCase::class.java)
    return res
}

actual fun getOpenUrlUseCase(default: UriHandler): OpenUrlUseCase {
    val inject =
        KoinJavaComponent.inject<OpenUrlUseCase>(
            OpenUrlUseCase::class.java,
            parameters = {
                parametersOf(default)
            },
        )
    val res by inject
    return res
}
