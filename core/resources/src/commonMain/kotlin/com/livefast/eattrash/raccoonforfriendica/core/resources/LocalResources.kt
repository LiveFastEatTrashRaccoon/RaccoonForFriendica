package com.livefast.eattrash.raccoonforfriendica.core.resources

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalResources: ProvidableCompositionLocal<CoreResources> =
    staticCompositionLocalOf {
        error("CompositionLocal CoreResources not found")
    }
