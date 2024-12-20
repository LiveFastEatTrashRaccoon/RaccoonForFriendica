package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler
import org.kodein.di.instance

fun getCustomUriHandler(fallback: UriHandler): CustomUriHandler {
    val res by RootDI.di.instance<UriHandler, CustomUriHandler>(arg = fallback)
    return res
}
