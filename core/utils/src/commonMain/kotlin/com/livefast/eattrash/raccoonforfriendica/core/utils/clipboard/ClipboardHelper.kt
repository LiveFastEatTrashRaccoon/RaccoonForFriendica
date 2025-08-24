package com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard

interface ClipboardHelper {
    suspend fun setText(text: String)

    suspend fun getText(): String?
}
