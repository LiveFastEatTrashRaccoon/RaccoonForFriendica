package com.livefast.eattrash.raccoonforfriendica.core.utils.appicon

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import kotlinx.coroutines.flow.StateFlow

sealed interface AppIconVariant {
    data object Default : AppIconVariant

    data object Alt : AppIconVariant
}

interface AppIconManager {
    val supportsMultipleIcons: Boolean
    val current: StateFlow<AppIconVariant>

    fun changeIcon(variant: AppIconVariant)
}

@Composable
fun AppIconVariant.toReadableName(): String =
    when (this) {
        AppIconVariant.Alt -> LocalStrings.current.appIconClassical
        AppIconVariant.Default -> LocalStrings.current.appIconDefault
    }

fun AppIconVariant.toInt(): Int =
    when (this) {
        AppIconVariant.Alt -> 1
        AppIconVariant.Default -> 0
    }

fun Int.toAppIconVariant(): AppIconVariant =
    when (this) {
        1 -> AppIconVariant.Alt
        else -> AppIconVariant.Default
    }
