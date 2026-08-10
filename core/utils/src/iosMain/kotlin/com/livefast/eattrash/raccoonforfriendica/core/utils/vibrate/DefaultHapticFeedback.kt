package com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate

import org.koin.core.annotation.Single
import platform.UIKit.UIImpactFeedbackGenerator

@Single
internal class DefaultHapticFeedback : HapticFeedback {
    override fun vibrate() {
        UIImpactFeedbackGenerator().apply {
            prepare()
            impactOccurred()
        }
    }
}
