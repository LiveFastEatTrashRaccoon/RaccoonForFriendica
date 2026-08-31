package com.livefast.eattrash.raccoonforfriendica.core.di.utils.di

import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.ClipboardHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.ClipboardHelperFactory
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandlerFactory
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.di.utils")
class UiDepsModule {

    @Single
    fun customUriHandlerFactory(scope: Scope): CustomUriHandlerFactory = object : CustomUriHandlerFactory {
        override fun create(fallbackHandler: UriHandler) = scope.get<CustomUriHandler> { parametersOf(fallbackHandler) }
    }

    @Single
    fun clipboardHelperFactory(scope: Scope): ClipboardHelperFactory = object : ClipboardHelperFactory {
        override fun create(clipboard: Clipboard) = scope.get<ClipboardHelper> { parametersOf(clipboard) }
    }
}
