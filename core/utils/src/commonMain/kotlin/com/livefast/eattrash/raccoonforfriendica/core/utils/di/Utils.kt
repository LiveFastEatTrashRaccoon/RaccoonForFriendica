package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import org.koin.core.module.Module

expect fun getImageLoaderProvider(): ImageLoaderProvider

expect val coreUtilsFileSystemModule: Module
