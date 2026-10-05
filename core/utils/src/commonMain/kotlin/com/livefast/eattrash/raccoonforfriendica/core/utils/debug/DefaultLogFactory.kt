package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import co.touchlab.kermit.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject


@ContributesBinding(AppScope::class)
@Inject
class DefaultLogFactory : LogFactory {

    override fun create(tag: String?): Log {
        val logger = Logger.withTag(tag.orEmpty())
        return DefaultLog(logger)
    }
}
