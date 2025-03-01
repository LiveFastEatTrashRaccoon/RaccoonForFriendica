package com.livefast.eattrash.raccoonforfriendica.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import chaintech.videoplayer.model.AudioPlayerConfig
import chaintech.videoplayer.model.VideoPlayerConfig
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import raccoonforfriendica.composeapp.generated.resources.Res
import raccoonforfriendica.composeapp.generated.resources.activitypub_small
import raccoonforfriendica.composeapp.generated.resources.atkinsonhyperlegible_bold
import raccoonforfriendica.composeapp.generated.resources.atkinsonhyperlegible_italic
import raccoonforfriendica.composeapp.generated.resources.atkinsonhyperlegible_regular
import raccoonforfriendica.composeapp.generated.resources.bluesky_small
import raccoonforfriendica.composeapp.generated.resources.diaspora_small
import raccoonforfriendica.composeapp.generated.resources.exo_bold
import raccoonforfriendica.composeapp.generated.resources.exo_italic
import raccoonforfriendica.composeapp.generated.resources.exo_light
import raccoonforfriendica.composeapp.generated.resources.exo_regular
import raccoonforfriendica.composeapp.generated.resources.flipboard_small
import raccoonforfriendica.composeapp.generated.resources.friendica_logo
import raccoonforfriendica.composeapp.generated.resources.friendica_small
import raccoonforfriendica.composeapp.generated.resources.gnusocial_small
import raccoonforfriendica.composeapp.generated.resources.gotosocial_small
import raccoonforfriendica.composeapp.generated.resources.ic_alt
import raccoonforfriendica.composeapp.generated.resources.ic_default
import raccoonforfriendica.composeapp.generated.resources.kbin_small
import raccoonforfriendica.composeapp.generated.resources.lemmy_small
import raccoonforfriendica.composeapp.generated.resources.mastodon_logo
import raccoonforfriendica.composeapp.generated.resources.mastodon_small
import raccoonforfriendica.composeapp.generated.resources.misskey_small
import raccoonforfriendica.composeapp.generated.resources.notosans_bold
import raccoonforfriendica.composeapp.generated.resources.notosans_italic
import raccoonforfriendica.composeapp.generated.resources.notosans_medium
import raccoonforfriendica.composeapp.generated.resources.notosans_regular
import raccoonforfriendica.composeapp.generated.resources.peertube_small
import raccoonforfriendica.composeapp.generated.resources.pixelfed_small
import raccoonforfriendica.composeapp.generated.resources.pleroma_small
import raccoonforfriendica.composeapp.generated.resources.wordpress_small

internal class SharedResources : CoreResources {
    // region Fonts
    override val atkinsonHyperlegible: FontFamily
        @Composable get() =
            FontFamily(
                Font(Res.font.atkinsonhyperlegible_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.atkinsonhyperlegible_bold, FontWeight.Bold, FontStyle.Normal),
                Font(Res.font.atkinsonhyperlegible_italic, FontWeight.Normal, FontStyle.Italic),
            )

    override val exo2: FontFamily
        @Composable get() =
            FontFamily(
                Font(Res.font.exo_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.exo_bold, FontWeight.Bold, FontStyle.Normal),
                Font(Res.font.exo_light, FontWeight.Light, FontStyle.Normal),
                Font(Res.font.exo_italic, FontWeight.Normal, FontStyle.Italic),
            )

    override val notoSans: FontFamily
        @Composable get() =
            FontFamily(
                Font(Res.font.notosans_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.notosans_bold, FontWeight.Bold, FontStyle.Normal),
                Font(Res.font.notosans_medium, FontWeight.Medium, FontStyle.Normal),
                Font(Res.font.notosans_italic, FontWeight.Normal, FontStyle.Italic),
            )
    // endregion

    // region App icons
    override val appIconDefault: Painter
        @Composable get() = painterResource(Res.drawable.ic_default)

    override val appIconAlt: Painter
        @Composable get() = painterResource(Res.drawable.ic_alt)
    // endregion

    // region Media player config
    override val videoPlayerConfig: VideoPlayerConfig =
        VideoPlayerConfig(isFullScreenEnabled = false)

    override val audioPlayerConfig: AudioPlayerConfig =
        AudioPlayerConfig(controlsBottomPadding = Spacing.s)
    // endregion

    // region Platform logos
    override val activityPubSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.activitypub_small)

    override val blueskySmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.bluesky_small)

    override val diasporaSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.diaspora_small)

    override val flipboardSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.flipboard_small)

    override val friendicaLogo: Painter
        @Composable get() = painterResource(Res.drawable.friendica_logo)

    override val friendicaSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.friendica_small)

    override val gnuSocialSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.gnusocial_small)

    override val gotoSocialSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.gotosocial_small)

    override val kbinSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.kbin_small)

    override val lemmySmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.lemmy_small)

    override val mastodonLogo: Painter
        @Composable get() = painterResource(Res.drawable.mastodon_logo)

    override val mastodonSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.mastodon_small)

    override val misskeySmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.misskey_small)

    override val peerTubeSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.peertube_small)

    override val pixelfedSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.pixelfed_small)

    override val pleromaSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.pleroma_small)

    override val wordPressSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.wordpress_small)

    // endregion
}
