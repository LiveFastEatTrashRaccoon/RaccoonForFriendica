package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.appicon.AppIconManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.appicon.DefaultAppIconManager
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal actual val nativeAppIconModule =
    DI.Module("NativeAppIconModule") {
        bind<AppIconManager> {
            singleton {
                DefaultAppIconManager(
                    context = instance(),
                    keyStore = instance(),
                )
            }
        }
    }
