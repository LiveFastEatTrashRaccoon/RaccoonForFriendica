package com.livefast.eattrash.raccoonforfriendica.core.l10n

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalStrings: ProvidableCompositionLocal<Strings> =
    staticCompositionLocalOf {
        error("CompositionLocal Strings not found")
    }
