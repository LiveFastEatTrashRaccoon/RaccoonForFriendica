package com.livefast.eattrash.raccoonforfriendica.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import chaintech.videoplayer.model.PlayerConfig
import chaintech.videoplayer.model.ScreenResize
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import raccoonforfriendica.composeapp.generated.resources.Res
import raccoonforfriendica.composeapp.generated.resources.atkinsonhyperlegible_bold
import raccoonforfriendica.composeapp.generated.resources.atkinsonhyperlegible_italic
import raccoonforfriendica.composeapp.generated.resources.atkinsonhyperlegible_regular
import raccoonforfriendica.composeapp.generated.resources.exo_bold
import raccoonforfriendica.composeapp.generated.resources.exo_italic
import raccoonforfriendica.composeapp.generated.resources.exo_light
import raccoonforfriendica.composeapp.generated.resources.exo_regular
import raccoonforfriendica.composeapp.generated.resources.friendica_logo
import raccoonforfriendica.composeapp.generated.resources.mastodon_logo
import raccoonforfriendica.composeapp.generated.resources.notosans_bold
import raccoonforfriendica.composeapp.generated.resources.notosans_italic
import raccoonforfriendica.composeapp.generated.resources.notosans_medium
import raccoonforfriendica.composeapp.generated.resources.notosans_regular

internal class SharedResources : CoreResources {
    override val atkinsonHyperlegible: FontFamily
        @Composable
        get() =
            FontFamily(
                Font(Res.font.atkinsonhyperlegible_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.atkinsonhyperlegible_bold, FontWeight.Bold, FontStyle.Normal),
                Font(Res.font.atkinsonhyperlegible_italic, FontWeight.Normal, FontStyle.Italic),
            )

    override val exo2: FontFamily
        @Composable
        get() =
            FontFamily(
                Font(Res.font.exo_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.exo_bold, FontWeight.Bold, FontStyle.Normal),
                Font(Res.font.exo_light, FontWeight.Light, FontStyle.Normal),
                Font(Res.font.exo_italic, FontWeight.Normal, FontStyle.Italic),
            )

    override val notoSans: FontFamily
        @Composable
        get() =
            FontFamily(
                Font(Res.font.notosans_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.notosans_bold, FontWeight.Bold, FontStyle.Normal),
                Font(Res.font.notosans_medium, FontWeight.Medium, FontStyle.Normal),
                Font(Res.font.notosans_italic, FontWeight.Normal, FontStyle.Italic),
            )

    override val friendicaLogo: Painter
        @Composable
        get() = painterResource(Res.drawable.friendica_logo)

    override val mastodonLogo: Painter
        @Composable
        get() = painterResource(Res.drawable.mastodon_logo)

    override fun getPlayerConfig(contentScale: ContentScale): PlayerConfig =
        PlayerConfig(
            isFullScreenEnabled = false,
            isMute = true,
            videoFitMode =
                if (contentScale == ContentScale.Fit) {
                    ScreenResize.FIT
                } else {
                    ScreenResize.FILL
                },
        )
}
