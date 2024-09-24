package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface EmojiRepository {
    suspend fun getAll(node: String? = null): List<EmojiModel>?

    /*
     * Due to a bug in Friendica, custom emojis are never returned for users (neither as single
     * entities nor as post creators).
     *
     * These two functions are a client-side workaround that retrieves the emojis if (and only if)
     * needed with an aggressive caching mechanism to avoid flooding the servers with unneeded calls.
     */
    suspend fun UserModel.withEmojisIfMissing(): UserModel

    // see comment above
    suspend fun TimelineEntryModel.withEmojisIfMissing(): TimelineEntryModel
}
