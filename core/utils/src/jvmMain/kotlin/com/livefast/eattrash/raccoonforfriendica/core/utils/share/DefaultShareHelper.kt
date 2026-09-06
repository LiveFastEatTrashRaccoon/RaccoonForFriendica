package com.livefast.eattrash.raccoonforfriendica.core.utils.share

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultShareHelper : ShareHelper {
    override val supportsShareImage = false

    override fun share(url: String, mimeType: String) {
        // TODO(jvm): implement
    }

    override fun shareImage(path: Any?, mimeType: String) {
        // TODO(jvm): implement
    }
}
