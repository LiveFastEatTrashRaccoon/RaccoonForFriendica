package com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate

import org.koin.core.annotation.Single

@Single
internal expect class DefaultHapticFeedback : HapticFeedback {
    override fun vibrate()
}
