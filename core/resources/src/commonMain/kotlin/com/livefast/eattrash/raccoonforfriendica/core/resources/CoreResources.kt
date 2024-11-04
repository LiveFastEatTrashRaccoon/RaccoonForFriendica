package com.livefast.eattrash.raccoonforfriendica.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontFamily

interface CoreResources {
    val atkinsonHyperlegible: FontFamily @Composable get
    val exo2: FontFamily @Composable get
    val notoSans: FontFamily @Composable get
    val friendicaLogo: Painter @Composable get
    val mastodonLogo: Painter @Composable get
}
