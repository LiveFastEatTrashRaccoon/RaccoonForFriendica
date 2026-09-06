package com.livefast.eattrash.raccoonforfriendica.core.utils.share

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultShareHelper : ShareHelper {
    override val supportsShareImage = false

    override fun share(url: String, mimeType: String) {
        val shareActivity = UIActivityViewController(listOf(url), null)
        val rvc = UIApplication.sharedApplication().keyWindow?.rootViewController
        rvc?.presentViewController(shareActivity, true, null)
    }

    override fun shareImage(path: Any?, mimeType: String) {
        val shareActivity =
            UIActivityViewController(
                listOf(UIImage(contentsOfFile = path?.toString().orEmpty())),
                null,
            )
        val rvc = UIApplication.sharedApplication().keyWindow?.rootViewController
        rvc?.presentViewController(shareActivity, true, null)
    }
}
