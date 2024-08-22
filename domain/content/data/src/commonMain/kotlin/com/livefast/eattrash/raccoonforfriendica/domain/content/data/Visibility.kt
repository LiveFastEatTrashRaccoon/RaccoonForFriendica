package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VisibilityOff
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
        Visibility.Direct -> Icons.AutoMirrored.Default.Message
        Visibility.Private -> Icons.Default.VisibilityOff
        Visibility.Public -> Icons.Default.Public
        Visibility.Unlisted -> Icons.Default.Lock
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
