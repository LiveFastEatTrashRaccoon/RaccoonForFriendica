package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources

sealed interface Visibility {
    // broadest visibility
    data object Public : Visibility

    // like "Public" but does not appear in timelines
    data object Unlisted : Visibility

    // only followers
    data object Private : Visibility

    // only mentions
    data object Direct : Visibility

    // only inside specific Circle
    data class Circle(val id: String? = null, val name: String? = null) : Visibility

    // like "Public" but only in local instance
    data object LocalPublic : Visibility

    // like "Unlisted" by only in local instance
    data object LocalUnlisted : Visibility
}

operator fun Visibility.compareTo(other: Visibility): Int = toSortKey().compareTo(other.toSortKey())

private fun Visibility.toSortKey() = when (this) {
    is Visibility.Circle -> 300
    Visibility.Direct -> 200
    Visibility.Private -> 300
    Visibility.Public -> 500
    Visibility.LocalPublic -> 450
    Visibility.Unlisted -> 400
    Visibility.LocalUnlisted -> 350
}

@Composable
fun Visibility.toIcon(coreResources: CoreResources): ImageVector = when (this) {
    Visibility.Direct -> coreResources.alternateEmail
    Visibility.Private -> coreResources.lock
    Visibility.Public -> coreResources.public
    Visibility.LocalPublic -> coreResources.cottage
    Visibility.Unlisted -> coreResources.lockOpen
    Visibility.LocalUnlisted -> coreResources.cottage
    is Visibility.Circle -> coreResources.workspaces
}

@Composable
fun Visibility.toReadableName(): String = when (this) {
    Visibility.Direct -> LocalStrings.current.visibilityDirect
    Visibility.Private -> LocalStrings.current.visibilityPrivate
    Visibility.Public -> LocalStrings.current.visibilityPublic
    Visibility.LocalPublic ->
        buildString {
            append(LocalStrings.current.timelineLocal)
            append(" (")
            append(LocalStrings.current.visibilityPublic)
            append(")")
        }
    Visibility.Unlisted -> LocalStrings.current.visibilityUnlisted
    Visibility.LocalUnlisted ->
        buildString {
            append(LocalStrings.current.timelineLocal)
            append(" (")
            append(LocalStrings.current.visibilityUnlisted)
            append(")")
        }
    is Visibility.Circle -> name ?: LocalStrings.current.visibilityCircle
}

fun Visibility.toInt() = when (this) {
    Visibility.LocalUnlisted -> 6
    Visibility.LocalPublic -> 5
    is Visibility.Circle -> 4
    Visibility.Private -> 3
    Visibility.Direct -> 2
    Visibility.Unlisted -> 1
    Visibility.Public -> 0
}

fun Int.toVisibility(): Visibility = when (this) {
    6 -> Visibility.LocalUnlisted
    5 -> Visibility.LocalPublic
    4 -> Visibility.Circle()
    3 -> Visibility.Private
    2 -> Visibility.Direct
    1 -> Visibility.Unlisted
    else -> Visibility.Public
}
