package com.livefast.eattrash.raccoonforfriendica.core.utils.appicon

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.annotation.Single

@Single
class DefaultAppIconManager : AppIconManager {
    override val supportsMultipleIcons = false
    override val current = MutableStateFlow(AppIconVariant.Default)

    override fun changeIcon(variant: AppIconVariant) {
        // no-op
    }
}
