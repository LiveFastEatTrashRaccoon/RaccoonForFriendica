package com.livefast.eattrash.raccoonforfriendica.core.utils

fun String?.ellipsize(
    length: Int = 100,
    ellipsis: String = "…",
): String {
    if (isNullOrEmpty() || length == 0) {
        return ""
    }
    if (this.length < length) {
        return this
    }
    return take(length - 1) + ellipsis
}

val String?.nodeName: String?
    get() =
        orEmpty()
            .let {
                if (it.contains('@')) {
                    it.substringAfter('@')
                } else {
                    ""
        }
    }.takeIf { it.isNotEmpty() }

fun Int.isNearTheEnd(list: List<*>): Boolean = this >= list.lastIndex - 5 || list.size <= 5
