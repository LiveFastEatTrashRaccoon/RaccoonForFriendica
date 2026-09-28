package com.livefast.eattrash.raccoonforfriendica.core.utils.share

interface ShareHelper {
    val supportsShareImage: Boolean

    fun share(url: String, mimeType: String = "text/plain")

    fun shareImage(path: Any?, mimeType: String = "image/*")
}
