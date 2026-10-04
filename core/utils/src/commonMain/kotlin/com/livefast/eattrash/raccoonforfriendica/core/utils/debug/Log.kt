package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

interface Log {
    fun v(message: String)
    fun d(message: String)
    fun i(message: String)
    fun w(message: String)
    fun e(message: String, throwable: Throwable? = null)
}
