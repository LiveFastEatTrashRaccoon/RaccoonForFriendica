package com.livefast.eattrash.raccoonforfriendica.feature.licences.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources

sealed interface LicenceItemType {
    data object Library : LicenceItemType

    data object Resource : LicenceItemType
}

@Composable
internal fun LicenceItemType.toIcon(): ImageVector = when (this) {
    LicenceItemType.Library -> LocalResources.current.sdk
    LicenceItemType.Resource -> LocalResources.current.palette
}
