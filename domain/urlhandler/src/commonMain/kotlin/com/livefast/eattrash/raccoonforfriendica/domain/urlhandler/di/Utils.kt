package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler

expect fun getCustomUriHandler(fallback: UriHandler): CustomUriHandler
