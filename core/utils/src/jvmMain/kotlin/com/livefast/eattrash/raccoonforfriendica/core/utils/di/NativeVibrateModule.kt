package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.DefaultHapticFeedback
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal actual val nativeVibrateModule =
    DI.Module("NativeVibrateModule") {
        bind<HapticFeedback> {
            singleton {
                DefaultHapticFeedback()
            }
        }
    }
