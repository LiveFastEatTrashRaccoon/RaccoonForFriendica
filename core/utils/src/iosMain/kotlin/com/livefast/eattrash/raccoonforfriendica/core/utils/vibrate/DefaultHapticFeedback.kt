package com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate

import org.koin.core.annotation.Single
import platform.UIKit.UIImpactFeedbackGenerator

@Single
class DefaultHapticFeedback : HapticFeedback {
    override fun vibrate() {
        UIImpactFeedbackGenerator().apply {
            prepare()
            impactOccurred()
        }
    }
}
