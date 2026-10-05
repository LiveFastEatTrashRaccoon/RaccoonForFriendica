package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class DefaultLogInitializer : LogInitializer {
    override fun initialize(isDebug: Boolean) {
        if (isDebug) {
            Logger.setLogWriters(platformLogWriter())
        } else {
            Logger.setLogWriters(emptyList())
        }
    }
}
