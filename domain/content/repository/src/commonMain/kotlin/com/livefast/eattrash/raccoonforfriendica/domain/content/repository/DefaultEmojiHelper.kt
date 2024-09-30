package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

internal class DefaultEmojiHelper(
    private val repository: EmojiRepository,
) : EmojiHelper {
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
        val emojis = repository.getAll(node)?.filterContainedIn(*texts).orEmpty()
        return copy(emojis = emojis)
    }

    override suspend fun TimelineEntryModel.withEmojisIfMissing(): TimelineEntryModel =
        copy(
            creator = creator?.withEmojisIfMissing(),
            inReplyTo =
                inReplyTo?.copy(
                    creator = inReplyTo?.creator?.withEmojisIfMissing(),
                ),
            reblog = reblog?.copy(reblog = null)?.withEmojisIfMissing(),
        )

    companion object {
        private val EMOJI_REGEX = Regex(":\\w+:")
    }
}

private fun List<EmojiModel>.filterContainedIn(vararg texts: String): List<EmojiModel> =
    filter { emoji ->
        val occurrence = ":${emoji.code}:"
        texts.any { it.contains(occurrence) }
    }
