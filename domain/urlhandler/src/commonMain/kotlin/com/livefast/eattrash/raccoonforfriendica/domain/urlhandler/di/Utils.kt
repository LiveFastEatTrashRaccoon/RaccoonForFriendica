package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler
import org.koin.core.parameter.parametersOf

@OptIn(DelicateDiApi::class)
@Composable
fun rememberCustomUriHandler(fallback: UriHandler) = remember {
    getByInjection(
        clazz = CustomUriHandler::class,
        parameters = { parametersOf(fallback) },
    )
}
