package com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard

import androidx.compose.ui.platform.Clipboard

internal class DefaultClipboardHelper(private val clipboard: Clipboard) : ClipboardHelper {
    override suspend fun setText(text: String) {
        // TODO(jvm): implement
        // no-op
    }

    override suspend fun getText(): String? {
        // TODO(jvm): implement
        return null
    }
}
