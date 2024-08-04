package com.github.akesiseli.raccoonforfriendica.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.github.akesiseli.raccoonforfriendica.core.resources.CoreResources
import org.jetbrains.compose.resources.Font
import raccoonforfriendica.composeapp.generated.resources.Res
import raccoonforfriendica.composeapp.generated.resources.exo_bold
import raccoonforfriendica.composeapp.generated.resources.exo_italic
import raccoonforfriendica.composeapp.generated.resources.exo_light
import raccoonforfriendica.composeapp.generated.resources.exo_regular
import raccoonforfriendica.composeapp.generated.resources.notosans_bold
import raccoonforfriendica.composeapp.generated.resources.notosans_italic
import raccoonforfriendica.composeapp.generated.resources.notosans_medium
import raccoonforfriendica.composeapp.generated.resources.notosans_regular

internal class SharedResources : CoreResources {
    override val notoSans: FontFamily
        @Composable
        get() =
            FontFamily(
                Font(Res.font.notosans_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.notosans_bold, FontWeight.Bold, FontStyle.Normal),
                Font(Res.font.notosans_medium, FontWeight.Medium, FontStyle.Normal),
                Font(Res.font.notosans_italic, FontWeight.Normal, FontStyle.Italic),
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
}
