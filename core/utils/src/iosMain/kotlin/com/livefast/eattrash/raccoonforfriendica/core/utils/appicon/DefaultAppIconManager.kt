package com.livefast.eattrash.raccoonforfriendica.core.utils.appicon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

@Single
internal actual class DefaultAppIconManager : AppIconManager {
    actual override val supportsMultipleIcons = false
    actual override val current: StateFlow<AppIconVariant> =
        MutableStateFlow(AppIconVariant.Default)

    actual override fun changeIcon(variant: AppIconVariant) {
        // no-op
    }
}
