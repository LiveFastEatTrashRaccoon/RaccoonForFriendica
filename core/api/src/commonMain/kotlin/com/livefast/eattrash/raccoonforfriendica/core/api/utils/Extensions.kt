package com.livefast.eattrash.raccoonforfriendica.core.api.utils

internal fun String.extractCursorFromLinkHeaderValue(): String? {
    val match = Regex("max_id=([^&>]+)").find(this)
    return match?.groupValues?.get(1)
}
