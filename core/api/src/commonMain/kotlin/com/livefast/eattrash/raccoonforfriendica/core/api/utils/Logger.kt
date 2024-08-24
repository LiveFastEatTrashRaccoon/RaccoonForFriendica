package com.livefast.eattrash.raccoonforfriendica.core.api.utils

import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.logDebug
import io.ktor.client.plugins.logging.Logger

internal val defaultLogger =
    object : Logger {
        override fun log(message: String) {
            logDebug(message)
        }
    }
