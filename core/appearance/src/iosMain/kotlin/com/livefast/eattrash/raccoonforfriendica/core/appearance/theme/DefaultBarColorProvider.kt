package com.livefast.eattrash.raccoonforfriendica.core.appearance.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

internal class DefaultBarColorProvider : BarColorProvider {
    override val isBarThemeSupported = false

    @Composable
    override fun setBarColorAccordingToTheme(
        theme: UiTheme,
        barTheme: UiBarTheme,
    ) {
        val isSystemInDarkTheme = isSystemInDarkTheme()
        LaunchedEffect(theme, isSystemInDarkTheme) {
            val style =
                when (theme) {
                    UiTheme.Light -> UIStatusBarStyleLightContent
                    UiTheme.Dark, UiTheme.Black -> UIStatusBarStyleDarkContent
                    UiTheme.Default ->
                        if (isSystemInDarkTheme) {
                            UIStatusBarStyleDarkContent
                        } else {
                            UIStatusBarStyleLightContent
                        }
                }
            UIApplication.sharedApplication().setStatusBarStyle(style)
        }
    }
}
