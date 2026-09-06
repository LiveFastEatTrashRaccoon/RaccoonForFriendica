package com.livefast.eattrash.raccoonforfriendica.core.di.utils

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalUiDeps: ProvidableCompositionLocal<UiDeps> =
    staticCompositionLocalOf {
        error("CompositionLocal UiDeps not found")
    }
