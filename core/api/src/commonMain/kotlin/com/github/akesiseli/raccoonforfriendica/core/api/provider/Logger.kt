package com.github.akesiseli.raccoonforfriendica.core.api.provider

import com.github.akesiseli.raccoonforfriendica.core.utils.debug.logDebug
import io.ktor.client.plugins.logging.Logger

internal val defaultLogger =
    object : Logger {
        override fun log(message: String) {
            logDebug(message)
        }
    }
