package com.livefast.eattrash.raccoonforfriendica.core.utils.imageload

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.FileSystemManager

internal class DefaultImageLoaderProvider(
    private val context: PlatformContext,
    private val fileSystemManager: FileSystemManager,
) : ImageLoaderProvider {
    private val imageLoader by lazy {
        ImageLoader
            .Builder(context)
            .components {
                val decoders = getNativeDecoders()
                for (decoder in decoders) {
                    add(decoder)
                }

                val fetchers = getNativeFetchers()
                for (fetcher in fetchers) {
                    add(fetcher)
                }
            }.memoryCache {
                MemoryCache
                    .Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }.diskCache {
                val path = fileSystemManager.getTempDir()
                DiskCache
                    .Builder()
                    .directory(path)
                    .maxSizePercent(0.02)
                    .build()
            }.crossfade(true)
            .build()
    }

    override fun provideImageLoader(): ImageLoader = imageLoader
}
