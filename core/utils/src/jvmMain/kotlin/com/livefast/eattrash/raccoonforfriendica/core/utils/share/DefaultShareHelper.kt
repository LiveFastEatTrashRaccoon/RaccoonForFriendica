package com.livefast.eattrash.raccoonforfriendica.core.utils.share

internal class DefaultShareHelper : ShareHelper {
    override val supportsShareImage = false

    override fun share(url: String, mimeType: String) {
        // TODO(jvm): implement
        // no-op
    }

    override fun shareImage(path: Any?, mimeType: String) {
        // TODO(jvm): implement
        // no-op
    }
}
