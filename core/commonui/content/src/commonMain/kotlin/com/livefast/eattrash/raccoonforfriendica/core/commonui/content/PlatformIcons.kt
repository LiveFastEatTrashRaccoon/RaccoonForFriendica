package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources

@Composable
fun String.toPlatformIcon(): Painter {
    val platformName = lowercase()
    return when (platformName) {
        "bluesky" -> LocalResources.current.blueskySmallLogo
        "diaspora" -> LocalResources.current.diasporaSmallLogo
        "flipboard" -> LocalResources.current.flipboardSmallLogo
        "friendica" -> LocalResources.current.friendicaSmallLogo
        "gnusocial" -> LocalResources.current.gnuSocialSmallLogo
        "gotosocial" -> LocalResources.current.gnuSocialSmallLogo
        "kbin", "mbin" -> LocalResources.current.kbinSmallLogo
        "lemmy" -> LocalResources.current.lemmySmallLogo
        "mastodon", "glitchsoc" -> LocalResources.current.mastodonSmallLogo
        "misskey", "sharkey" -> LocalResources.current.misskeySmallLogo
        "peertube" -> LocalResources.current.peerTubeSmallLogo
        "pixelfed" -> LocalResources.current.pixelfedSmallLogo
        "pleroma", "akkoma" -> LocalResources.current.pleromaSmallLogo
        "wordpress" -> LocalResources.current.wordPressSmallLogo
        else -> LocalResources.current.activityPubSmallLogo
    }
}
