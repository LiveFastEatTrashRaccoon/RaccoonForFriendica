package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler
import org.koin.core.parameter.parametersOf

fun getCustomUriHandler(fallback: UriHandler): CustomUriHandler =
    getByInjection(clazz = CustomUriHandler::class, parameters = { parametersOf(fallback) })
