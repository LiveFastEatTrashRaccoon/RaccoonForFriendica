package com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard

import androidx.compose.ui.platform.Clipboard

interface ClipboardHelper {
    suspend fun setText(text: String)

    suspend fun getText(): String?
}

fun interface ClipboardHelperFactory {
    fun create(clipboard: Clipboard): ClipboardHelper
}
