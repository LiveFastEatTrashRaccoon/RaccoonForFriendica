package com.livefast.eattrash.raccoonforfriendica.core.utils.imageload

import androidx.compose.ui.graphics.ImageBitmap

data class BlurHashParams(val hash: String, val width: Int, val height: Int)

interface BlurHashRepository {
    suspend fun preload(params: BlurHashParams)

    suspend fun get(params: BlurHashParams): ImageBitmap?
}
