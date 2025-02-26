package com.livefast.eattrash.raccoonforfriendica.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontFamily
import chaintech.videoplayer.model.AudioPlayerConfig
import chaintech.videoplayer.model.VideoPlayerConfig

interface CoreResources {
    // region Fonts
    val atkinsonHyperlegible: FontFamily @Composable get
    val exo2: FontFamily @Composable get
    val notoSans: FontFamily @Composable get
    // endregion

    // region App icons
    val appIconDefault: Painter @Composable get
    val appIconAlt: Painter @Composable get
    // endregion

    // region Media player config
    val videoPlayerConfig: VideoPlayerConfig
    val audioPlayerConfig: AudioPlayerConfig
    // endregion

    // region Platform logos
    val activityPubSmallLogo: Painter @Composable get
    val diasporaSmallLogo: Painter @Composable get
    val flipboardSmallLogo: Painter @Composable get
    val friendicaLogo: Painter @Composable get
    val friendicaSmallLogo: Painter @Composable get
    val gnuSocialSmallLogo: Painter @Composable get
    val gotoSocialSmallLogo: Painter @Composable get
    val kbinSmallLogo: Painter @Composable get
    val lemmySmallLogo: Painter @Composable get
    val mastodonLogo: Painter @Composable get
    val mastodonSmallLogo: Painter @Composable get
    val misskeySmallLogo: Painter @Composable get
    val peerTubeSmallLogo: Painter @Composable get
    val pixelfedSmallLogo: Painter @Composable get
    val pleromaSmallLogo: Painter @Composable get
    val wordPressSmallLogo: Painter @Composable get
    // endregion
}
