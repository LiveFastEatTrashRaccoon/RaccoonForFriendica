package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel

interface EmojiRepository {
    suspend fun getAll(node: String? = null, refresh: Boolean = false): List<EmojiModel>?
}
