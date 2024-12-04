package com.livefast.eattrash.raccoonforfriendica.core.utils.appicon

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

@Single
internal expect class DefaultAppIconManager : AppIconManager {
    override val supportsMultipleIcons: Boolean
    override val current: StateFlow<AppIconVariant>

    override fun changeIcon(variant: AppIconVariant)
}
