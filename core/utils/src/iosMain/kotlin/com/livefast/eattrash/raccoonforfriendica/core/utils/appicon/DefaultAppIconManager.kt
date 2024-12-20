package com.livefast.eattrash.raccoonforfriendica.core.utils.appicon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class DefaultAppIconManager : AppIconManager {
    override val supportsMultipleIcons = false
    override val current: StateFlow<AppIconVariant> =
        MutableStateFlow(AppIconVariant.Default)

    override fun changeIcon(variant: AppIconVariant) {
        // no-op
    }
}
