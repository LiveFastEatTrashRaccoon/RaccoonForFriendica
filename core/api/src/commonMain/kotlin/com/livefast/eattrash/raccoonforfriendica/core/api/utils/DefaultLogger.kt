package com.livefast.eattrash.raccoonforfriendica.core.api.utils

import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.LogFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.ktor.client.plugins.logging.Logger

@ContributesBinding(AppScope::class)
@Inject
class DefaultLogger(logFactory: LogFactory) : Logger {
    private val log = logFactory.create("core.api")

    override fun log(message: String) {
        log.v { message }
    }
}
