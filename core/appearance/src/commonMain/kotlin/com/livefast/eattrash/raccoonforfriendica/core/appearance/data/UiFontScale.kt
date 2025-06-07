package com.livefast.eattrash.raccoonforfriendica.core.appearance.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface UiFontScale {
    data object Largest : UiFontScale

    data object Larger : UiFontScale

    data object Normal : UiFontScale

    data object Smaller : UiFontScale

    data object Smallest : UiFontScale
}

fun UiFontScale.toScaleFactor(): Float = when (this) {
    UiFontScale.Largest -> 1.25f
    UiFontScale.Larger -> 1.2f
    UiFontScale.Normal -> 1.15f
    UiFontScale.Smaller -> 1.1f
    UiFontScale.Smallest -> 1f
}

fun UiFontScale.toInt(): Int = when (this) {
    UiFontScale.Normal -> 0
    UiFontScale.Largest -> 1
    UiFontScale.Larger -> 2
    UiFontScale.Smaller -> 3
    UiFontScale.Smallest -> 4
}

fun Int.toUiFontScale(): UiFontScale = when (this) {
    1 -> UiFontScale.Normal
    2 -> UiFontScale.Larger
    3 -> UiFontScale.Smaller
    4 -> UiFontScale.Smallest
    else -> UiFontScale.Normal
}

@Composable
fun UiFontScale.toReadableName(): String = when (this) {
    UiFontScale.Largest -> LocalStrings.current.fontScaleLargest
    UiFontScale.Larger -> LocalStrings.current.fontScaleLarger
    UiFontScale.Normal -> LocalStrings.current.fontScaleNormal
    UiFontScale.Smaller -> LocalStrings.current.fontScaleSmaller
    UiFontScale.Smallest -> LocalStrings.current.fontScaleSmallest
}
