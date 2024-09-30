package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

/*
 * Due to a bug in Friendica, custom emojis are never returned for users (neither as single
 * entities nor as post creators).
 *
 * This helper is a client-side workaround that retrieves the emojis if (and only if) needed.
 */
interface EmojiHelper {
    suspend fun UserModel.withEmojisIfMissing(): UserModel

    suspend fun TimelineEntryModel.withEmojisIfMissing(): TimelineEntryModel
}
