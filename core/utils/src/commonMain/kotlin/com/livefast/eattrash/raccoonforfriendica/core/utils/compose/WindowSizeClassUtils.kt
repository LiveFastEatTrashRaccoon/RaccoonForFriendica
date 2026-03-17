package com.livefast.eattrash.raccoonforfriendica.core.utils.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable

@Composable
fun isWidthSizeClassEqualOrAbove(other: WindowWidthSizeClass): Boolean {
    val current = getWindowSizeClass()?.widthSizeClass ?: WindowWidthSizeClass.Compact
    return current >= other
}

@Composable
fun isWidthSizeClassBelow(other: WindowWidthSizeClass): Boolean {
    val current = getWindowSizeClass()?.widthSizeClass ?: WindowWidthSizeClass.Compact
    return current < other
}

@Composable
fun WindowInsets.optimizedForLargeScreens(): WindowInsets {
    return if (isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)) {
        this
    } else {
        WindowInsets(0, 0, 0, 0)
    }
}
