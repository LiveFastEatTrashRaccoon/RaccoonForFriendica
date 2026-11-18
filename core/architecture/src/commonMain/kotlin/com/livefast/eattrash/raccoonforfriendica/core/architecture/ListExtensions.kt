package com.livefast.eattrash.raccoonforfriendica.core.architecture

/**
 * Returns a new list where any item matching a given [matcher] is transformed by invoking [block].
 */
fun <T> List<T>.updateMatching(matcher: (T) -> Boolean, block: (T) -> T): List<T> = map {
    if (matcher(it)) {
        block(it)
    } else {
        it
    }
}

/**
 * Returns a new list excluding any item matching [matcher].
 */
fun <T> List<T>.removeMatching(matcher: (T) -> Boolean): List<T> = filterNot { matcher(it) }
