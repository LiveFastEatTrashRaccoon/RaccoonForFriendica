package com.livefast.eattrash.raccoonforfriendica.core.utils.imageload

interface ImagePreloadManager {
    fun preload(url: String)

    fun remove(url: String)
}
