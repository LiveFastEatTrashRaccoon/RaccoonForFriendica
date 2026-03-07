package com.livefast.eattrash.raccoonforfriendica.core.utils.gallery

import androidx.compose.runtime.Composable

internal class DefaultGalleryHelper : GalleryHelper {
    override val supportsCustomPath = false

    override fun saveToGallery(
        bytes: ByteArray,
        name: String,
        additionalPathSegment: String?,
    ): Any? {
        // TODO(jvm): implement
        return null
    }

    @Composable
    override fun getImageFromGallery(result: (ByteArray) -> Unit) {
        // TODO(jvm): implement
        // no-op
    }
}
