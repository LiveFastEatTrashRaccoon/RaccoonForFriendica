package com.livefast.eattrash.raccoonforfriendica.core.utils.compose

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun getWindowSizeClass(): WindowSizeClass? {
    val activity = LocalActivity.current
    checkNotNull(activity) { return null }
    return calculateWindowSizeClass(activity)
}
