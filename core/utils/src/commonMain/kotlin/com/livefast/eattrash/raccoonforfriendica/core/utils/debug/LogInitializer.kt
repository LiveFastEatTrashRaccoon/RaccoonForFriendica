package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

/**
 * Entry point for log initialization and configuration.
 */
interface LogInitializer {
    /**
     * Initialize the log system.
     *
     * @param isDebug whether this is a debug build or not
     */
    fun initialize(isDebug: Boolean)
}

