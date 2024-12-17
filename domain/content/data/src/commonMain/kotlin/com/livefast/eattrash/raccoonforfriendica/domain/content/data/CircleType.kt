package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface CircleType {
    data object UserDefined : CircleType

    data object Predefined : CircleType

    data object Group : CircleType

    data object Other : CircleType
}

@Composable
fun CircleType.toIcon(): ImageVector =
    when (this) {
        CircleType.Group -> Icons.Default.Group
        CircleType.Predefined -> Icons.Default.Lightbulb
        CircleType.UserDefined -> Icons.Default.Workspaces
        CircleType.Other -> Icons.Default.Circle
    }

@Composable
fun CircleType.toReadableName(): String =
    when (this) {
        CircleType.Group -> LocalStrings.current.circleTypeGroup
        CircleType.Predefined -> LocalStrings.current.circleTypePredefined
        CircleType.UserDefined -> LocalStrings.current.circleTypeUserDefined
        CircleType.Other -> LocalStrings.current.itemOther
    }
