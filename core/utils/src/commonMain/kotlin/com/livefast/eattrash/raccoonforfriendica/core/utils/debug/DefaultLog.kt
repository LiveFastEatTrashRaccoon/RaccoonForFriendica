package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import co.touchlab.kermit.Logger

class DefaultLog(
    private val logger: Logger,
) : Log {
    override fun v(message: String) {
        logger.v(messageString = message)
    }

    override fun d(message: String) {
        logger.d(messageString = message)
    }

    override fun i(message: String) {
        logger.i(messageString = message)
    }

    override fun w(message: String) {
        logger.w(messageString = message)
    }

    override fun e(message: String, throwable: Throwable?) {
        logger.e(messageString = message, throwable = throwable)
    }
}
