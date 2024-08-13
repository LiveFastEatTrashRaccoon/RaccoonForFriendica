package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import org.koin.core.module.Module

expect fun getImageLoaderProvider(): ImageLoaderProvider

expect fun getGalleryHelper(): GalleryHelper

expect val coreUtilsFileSystemModule: Module

expect val coreUtilsGalleryModule: Module
