package com.github.akesiseli.raccoonforfriendica.core.utils.di

import androidx.compose.ui.platform.UriHandler
import com.github.akesiseli.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.github.akesiseli.raccoonforfriendica.core.utils.url.UrlManager
import org.koin.core.module.Module

expect fun getImageLoaderProvider(): ImageLoaderProvider

expect fun getUrlManager(default: UriHandler): UrlManager

expect val coreUtilsFileSystemModule: Module
