package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

actual fun getCustomUriHandler(fallback: UriHandler): CustomUriHandler = DomainUrlHandlerDiHelper.getCustomUriHandler(fallback)

internal object DomainUrlHandlerDiHelper : KoinComponent {
    fun getCustomUriHandler(default: UriHandler): CustomUriHandler {
        val res by inject<CustomUriHandler>(
            parameters = {
                parametersOf(default)
            },
        )
        return res
    }
}
