package com.github.akesiseli.raccoonforfriendica.core.appearance.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.github.akesiseli.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.github.akesiseli.raccoonforfriendica.core.appearance.data.UiTheme
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

class DefaultBarColorProvider : BarColorProvider {
    @Composable
    override fun setBarColorAccordingToTheme(
        theme: UiTheme,
        transparent: UiBarTheme,
    ) {
        LaunchedEffect(theme) {
            val style =
                when {
                    theme == UiTheme.Light -> UIStatusBarStyleLightContent
                    else -> UIStatusBarStyleDarkContent
                }
            UIApplication.sharedApplication().setStatusBarStyle(style)
        }
    }
}
