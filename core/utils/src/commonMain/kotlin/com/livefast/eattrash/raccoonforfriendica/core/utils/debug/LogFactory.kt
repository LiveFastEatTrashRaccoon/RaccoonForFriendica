package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

/**
 * Factory to create [Log] instances.
 */
interface LogFactory {
    /**
     * Create a new [Log] instance with a tag.
     *
     * @param tag optional string used as a tag
     */
    fun create(tag: String? = null): Log
}
