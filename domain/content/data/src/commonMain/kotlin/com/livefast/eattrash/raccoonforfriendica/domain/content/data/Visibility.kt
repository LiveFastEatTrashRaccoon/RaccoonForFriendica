package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface Visibility {
    data object Public : Visibility

    data object Unlisted : Visibility

    data object Private : Visibility

    data object Direct : Visibility

    data class Circle(
        val id: String? = null,
        val name: String? = null,
    ) : Visibility
}

@Composable
fun Visibility.toIcon(): ImageVector =
    when (this) {
        Visibility.Direct -> Icons.Default.AlternateEmail
        Visibility.Private -> Icons.Default.Lock
        Visibility.Public -> Icons.Default.Public
        Visibility.Unlisted -> Icons.Default.LockOpen
        is Visibility.Circle -> Icons.Default.Workspaces
    }

@Composable
fun Visibility.toReadableName(): String =
    when (this) {
        Visibility.Direct -> LocalStrings.current.visibilityDirect
        Visibility.Private -> LocalStrings.current.visibilityPrivate
        Visibility.Public -> LocalStrings.current.visibilityPublic
        Visibility.Unlisted -> LocalStrings.current.visibilityUnlisted
        is Visibility.Circle -> name ?: LocalStrings.current.visibilityCircle
    }

fun Visibility.toInt() =
    when (this) {
        is Visibility.Circle -> 4
        Visibility.Private -> 3
        Visibility.Direct -> 2
        Visibility.Unlisted -> 1
        Visibility.Public -> 0
    }

fun Int.toVisibility(): Visibility =
    when (this) {
        4 -> Visibility.Circle()
        3 -> Visibility.Private
        2 -> Visibility.Direct
        1 -> Visibility.Unlisted
        else -> Visibility.Public
    }
