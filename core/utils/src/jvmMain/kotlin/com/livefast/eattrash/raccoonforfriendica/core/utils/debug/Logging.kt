package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

private val LOGGER = Logger.getLogger("Raccoon")

actual fun logDebug(message: String) {
    LOGGER.log(LogRecord(Level.FINE, message))
}
