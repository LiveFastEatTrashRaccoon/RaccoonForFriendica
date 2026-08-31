package com.livefast.eattrash.raccoonforfriendica.core.utils.appicon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

@Single
internal class DefaultAppIconManager : AppIconManager {
    override val current: StateFlow<AppIconVariant> = MutableStateFlow(AppIconVariant.Default)

    override val supportsMultipleIcons: Boolean = false

    override fun changeIcon(variant: AppIconVariant) {
        // TODO(jvm): implement
    }
}
