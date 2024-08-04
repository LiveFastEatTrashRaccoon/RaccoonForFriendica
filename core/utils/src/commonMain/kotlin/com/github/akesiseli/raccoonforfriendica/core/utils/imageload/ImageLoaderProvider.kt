package com.github.akesiseli.raccoonforfriendica.core.utils.imageload

import coil3.ImageLoader

interface ImageLoaderProvider {
    fun provideImageLoader(): ImageLoader
}
