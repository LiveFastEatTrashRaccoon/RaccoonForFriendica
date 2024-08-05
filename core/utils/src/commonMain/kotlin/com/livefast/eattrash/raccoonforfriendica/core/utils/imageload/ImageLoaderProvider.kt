package com.livefast.eattrash.raccoonforfriendica.core.utils.imageload

import coil3.ImageLoader

interface ImageLoaderProvider {
    fun provideImageLoader(): ImageLoader
}
