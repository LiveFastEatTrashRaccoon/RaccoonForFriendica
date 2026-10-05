package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import co.touchlab.kermit.Logger

class DefaultLog(
    private val logger: Logger,
) : Log {
    override fun v(message: () -> String) {
        logger.v(message = message)
    }

    override fun d(message: () -> String) {
        logger.d(message = message)
    }

    override fun i(message: () -> String) {
        logger.i(message = message)
    }

    override fun w(message: () -> String) {
        logger.w(message = message)
    }

    override fun e(throwable: Throwable?, message: () -> String) {
        logger.e(throwable = throwable, message = message)
    }
}
