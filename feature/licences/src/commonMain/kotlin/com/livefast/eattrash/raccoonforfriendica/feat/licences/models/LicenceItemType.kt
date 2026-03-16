package com.livefast.eattrash.raccoonforfriendica.feat.licences.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources

sealed interface LicenceItemType {
    data object Library : LicenceItemType

    data object Resource : LicenceItemType
}

@Composable
internal fun LicenceItemType.toIcon(coreResources: CoreResources): ImageVector = when (this) {
    LicenceItemType.Library -> coreResources.api
    LicenceItemType.Resource -> coreResources.palette
}
