package com.livefast.eattrash.raccoonforfriendica.core.utils.appicon

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultAppIconManager : AppIconManager {
    override val current: StateFlow<AppIconVariant> = MutableStateFlow(AppIconVariant.Default)

    override val supportsMultipleIcons: Boolean = false

    override fun changeIcon(variant: AppIconVariant) {
        // TODO(ios): implement
    }
}
