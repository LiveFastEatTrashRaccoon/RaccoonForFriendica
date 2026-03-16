package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources

sealed interface CircleType {
    data object UserDefined : CircleType

    data object Predefined : CircleType

    data object Group : CircleType

    data object Other : CircleType
}

@Composable
fun CircleType.toIcon(coreResources: CoreResources): ImageVector = when (this) {
    CircleType.Group -> coreResources.group
    CircleType.Predefined -> coreResources.lightbulb
    CircleType.UserDefined -> coreResources.workspaces
    CircleType.Other -> coreResources.circle
}

@Composable
fun CircleType.toReadableName(): String = when (this) {
    CircleType.Group -> LocalStrings.current.circleTypeGroup
    CircleType.Predefined -> LocalStrings.current.circleTypePredefined
    CircleType.UserDefined -> LocalStrings.current.circleTypeUserDefined
    CircleType.Other -> LocalStrings.current.itemOther
}
