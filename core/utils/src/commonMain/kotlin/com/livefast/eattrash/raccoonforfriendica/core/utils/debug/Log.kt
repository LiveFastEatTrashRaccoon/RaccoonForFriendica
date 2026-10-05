package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

/**
 * Entry point for app logging.
 */
interface Log {
    /**
     * Log a verbose message.
     *
     * @param message message producer
     */
    fun v(message: () -> String)

    /**
     * Log a debug message.
     *
     * @param message message producer
     */
    fun d(message: () -> String)

    /**
     * Log a informative message.
     *
     * @param message message producer
     */
    fun i(message: () -> String)

    /**
     * Log a warning message.
     *
     * @param message message producer
     */
    fun w(message: () -> String)

    /**
     * Log an error message.
     *
     * @param message message producer
     */
    fun e(throwable: Throwable? = null, message: () -> String)
}
