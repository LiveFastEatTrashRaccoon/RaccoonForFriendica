package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultEmojiRepository(
    private val provider: ServiceProvider,
    private val otherProvider: ServiceProvider,
    private val cache: LruCache<String, List<EmojiModel>> = LruCache.factory(20),
) : EmojiRepository {
    private val otherMutex = Mutex()

    override suspend fun getAll(node: String?, refresh: Boolean): List<EmojiModel> {
        val key = node.orEmpty()
        val cachedValue = cache.get(key)
        return if (cachedValue != null && !refresh) {
            cachedValue
        } else {
            retrieve(node).orEmpty().also {
                cache.put(key, it)
            }
        }
    }

    private suspend fun retrieve(node: String?): List<EmojiModel>? = try {
        val res =
            if (node == null) {
                provider.instance.getCustomEmojis()
            } else {
                otherMutex.withLock {
                    otherProvider.changeNode(node)
                    otherProvider.instance.getCustomEmojis()
                }
            }
        res.map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }
}
