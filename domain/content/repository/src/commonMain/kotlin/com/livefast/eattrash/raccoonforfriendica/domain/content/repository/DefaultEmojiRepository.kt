package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultEmojiRepository(
    private val provider: ServiceProvider,
    private val otherProvider: ServiceProvider,
) : EmojiRepository {
    private val cache = LruCache<String, List<EmojiModel>>(20)

    override suspend fun getAll(node: String?): List<EmojiModel>? {
        val key = node.orEmpty()
        return if (cache.containsKey(key)) {
            cache.get(key)
        } else {
            retrieve(node).orEmpty().also {
                cache.put(key, it)
            }
        }
    }

    override suspend fun UserModel.withEmojisIfMissing(): UserModel {
        if (emojis.isNotEmpty()) {
            return this
        }
        val texts =
            arrayOf(
                displayName.orEmpty(),
                bio.orEmpty(),
            )
        if (texts.none { it.contains(EMOJI_REGEX) }) {
            return this
        }

        val node = handle.nodeName
        val emojis = getAll(node)?.filterContainedIn(*texts).orEmpty()
        return copy(emojis = emojis)
    }

    override suspend fun TimelineEntryModel.withEmojisIfMissing(): TimelineEntryModel =
        copy(
            creator = creator?.withEmojisIfMissing(),
            inReplyTo =
                inReplyTo?.copy(
                    creator = reblog?.creator?.withEmojisIfMissing(),
                ),
            reblog =
                reblog?.copy(
                    creator = reblog?.creator?.withEmojisIfMissing(),
                ),
        )

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

    companion object {
        private val EMOJI_REGEX = Regex(":\\w+:")
    }
}

private fun List<EmojiModel>.filterContainedIn(vararg texts: String): List<EmojiModel> =
    filter { emoji ->
        val occurrence = ":${emoji.code}:"
        texts.any { it.contains(occurrence) }
    }
