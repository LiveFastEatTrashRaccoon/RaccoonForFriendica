package com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import platform.UIKit.UIImpactFeedbackGenerator

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultHapticFeedback : HapticFeedback {
    override fun vibrate() {
        UIImpactFeedbackGenerator().apply {
            prepare()
            impactOccurred()
        }
    }
}
