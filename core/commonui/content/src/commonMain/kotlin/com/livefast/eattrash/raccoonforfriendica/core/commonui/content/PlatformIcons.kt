package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.getCoreResources

@Composable
fun String.toPlatformIcon(): Painter {
    val resources = remember { getCoreResources() }
    val platformName = lowercase()
    return when (platformName) {
        "diaspora" -> resources.diasporaSmallLogo
        "flipboard" -> resources.flipboardSmallLogo
        "friendica" -> resources.friendicaSmallLogo
        "gnusocial" -> resources.gnuSocialSmallLogo
        "gotosocial" -> resources.gnuSocialSmallLogo
        "kbin", "mbin" -> resources.kbinSmallLogo
        "lemmy" -> resources.lemmySmallLogo
        "mastodon", "glitchsoc" -> resources.mastodonSmallLogo
        "misskey", "sharkey" -> resources.misskeySmallLogo
        "peertube" -> resources.peerTubeSmallLogo
        "pixelfed" -> resources.pixelfedSmallLogo
        "pleroma", "akkoma" -> resources.pleromaSmallLogo
        "wordpress" -> resources.wordPressSmallLogo
        else -> resources.activityPubSmallLogo
    }
}
