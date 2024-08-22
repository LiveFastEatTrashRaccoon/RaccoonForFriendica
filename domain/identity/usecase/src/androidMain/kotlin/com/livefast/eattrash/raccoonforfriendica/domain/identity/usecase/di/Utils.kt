package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.CustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent

actual fun getSetupAccountUseCase(): SetupAccountUseCase {
    val res by KoinJavaComponent.inject<SetupAccountUseCase>(SetupAccountUseCase::class.java)
    return res
}

actual fun getCustomUriHandler(fallback: UriHandler): CustomUriHandler {
    val inject =
        KoinJavaComponent.inject<CustomUriHandler>(
            CustomUriHandler::class.java,
            parameters = {
                parametersOf(fallback)
            },
        )
    val res by inject
    return res
}
