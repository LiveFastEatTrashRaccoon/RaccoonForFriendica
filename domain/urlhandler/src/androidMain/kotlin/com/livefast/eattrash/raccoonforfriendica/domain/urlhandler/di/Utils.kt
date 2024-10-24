package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent

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
