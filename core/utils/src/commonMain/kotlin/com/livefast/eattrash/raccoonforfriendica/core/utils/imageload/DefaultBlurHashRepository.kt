package com.livefast.eattrash.raccoonforfriendica.core.utils.imageload

import androidx.compose.ui.graphics.ImageBitmap
import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultBlurHashRepository(
    private val decoder: BlurHashDecoder,
    private val cache: LruCache<String, ImageBitmap> = LruCache.factory(CACHE_SIZE),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BlurHashRepository {
    override suspend fun preload(params: BlurHashParams) {
        val key = params.hash
        if (!cache.containsKey(key)) {
            decode(
                blurHash = key,
                width = params.width,
                height = params.height,
            )
        }
    }

    override suspend fun get(params: BlurHashParams): ImageBitmap? {
        val key = params.hash
        val cachedValue = cache.get(key)
        return cachedValue
            ?: decode(
                blurHash = key,
                width = params.width,
                height = params.height,
            )?.also {
                cache.put(key, it)
            }
    }

    private suspend fun decode(
        blurHash: String,
        width: Int,
        height: Int,
    ): ImageBitmap? =
        withContext(dispatcher) {
            decoder.decode(
                blurHash = blurHash,
                width = width,
                height = height,
            )
        }

    companion object {
        private const val CACHE_SIZE = 20
    }
}
