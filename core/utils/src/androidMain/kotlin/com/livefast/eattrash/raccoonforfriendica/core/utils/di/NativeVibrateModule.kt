package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.DefaultHapticFeedback
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import org.koin.dsl.module

internal actual val nativeVibrateModule =
    module{
        single<HapticFeedback> {
            DefaultHapticFeedback(
                context = get(),
            )
        }
    }
