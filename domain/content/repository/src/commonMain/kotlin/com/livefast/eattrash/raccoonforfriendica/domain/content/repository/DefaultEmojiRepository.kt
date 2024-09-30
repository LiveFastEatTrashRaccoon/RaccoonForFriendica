package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultEmojiRepository(
    private val provider: ServiceProvider,
    private val otherProvider: ServiceProvider,
) : EmojiRepository {
    private val cache = LruCache<String, List<EmojiModel>>(20)

    override suspend fun getAll(
        node: String?,
        refresh: Boolean,
    ): List<EmojiModel>? {
        val key = node.orEmpty()
        return if (cache.containsKey(key) && !refresh) {
            cache.get(key)
        } else {
            retrieve(node).orEmpty().also {
                cache.put(key, it)
            }
        }
    }

    private suspend fun retrieve(node: String?): List<EmojiModel>? =
        withContext(Dispatchers.IO) {
            runCatching {
                val res =
                    if (node == null) {
                        provider.instance.getCustomEmojis()
                    } else {
                        otherProvider.changeNode(node)
                        otherProvider.instance.getCustomEmojis()
                    }
                res.map { it.toModel() }
            }.getOrElse { null }
        }
}
