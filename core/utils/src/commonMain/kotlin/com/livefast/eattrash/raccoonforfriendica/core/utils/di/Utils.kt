package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.UrlManager
import org.koin.core.module.Module

expect fun getImageLoaderProvider(): ImageLoaderProvider

expect fun getUrlManager(default: UriHandler): UrlManager

expect val coreUtilsFileSystemModule: Module
