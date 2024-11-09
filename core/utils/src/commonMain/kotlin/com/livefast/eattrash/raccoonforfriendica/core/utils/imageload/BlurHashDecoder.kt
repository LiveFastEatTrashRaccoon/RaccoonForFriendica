package com.livefast.eattrash.raccoonforfriendica.core.utils.imageload

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap

@Stable
interface BlurHashDecoder {
    /**
     * Clear calculations stored in memory cache.
     * The cache is not big, but will increase when many image sizes are used,
     * if the app needs memory it is recommended to clear it.
     */
    suspend fun clearCache()

    /**
     * Decode a blur hash into a new bitmap.
     *
     * @param useCache use in memory cache for the calculated math, reused by images with same size.
     * if the cache does not exist yet it will be created and populated with new calculations.
     */
    suspend fun decode(
        blurHash: String?,
        width: Int,
        height: Int,
        punch: Float = 1f,
        useCache: Boolean = true,
    ): ImageBitmap?
}
