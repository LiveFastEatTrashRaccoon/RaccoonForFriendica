package com.livefast.eattrash.raccoonforfriendica.core.api.utils

internal fun String.extractCursorFromLinkHeaderValue(): String? {
    val match = Regex("max_id=(?<maxId>\\d+)>").find(this)
    return match?.groups?.get("maxId")?.value
}
