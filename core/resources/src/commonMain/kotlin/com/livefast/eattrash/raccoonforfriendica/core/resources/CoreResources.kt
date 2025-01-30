package com.livefast.eattrash.raccoonforfriendica.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontFamily
import chaintech.videoplayer.model.AudioPlayerConfig
import chaintech.videoplayer.model.VideoPlayerConfig

interface CoreResources {
    val atkinsonHyperlegible: FontFamily @Composable get
    val exo2: FontFamily @Composable get
    val notoSans: FontFamily @Composable get
    val friendicaLogo: Painter @Composable get
    val mastodonLogo: Painter @Composable get
    val appIconDefault: Painter @Composable get
    val appIconAlt: Painter @Composable get
    val videoPlayerConfig: VideoPlayerConfig
    val audioPlayerConfig: AudioPlayerConfig
}
