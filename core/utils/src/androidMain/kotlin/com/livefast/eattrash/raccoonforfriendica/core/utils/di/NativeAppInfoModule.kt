package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.DefaultAppInfoRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal actual val nativeAppInfoModule =
    DI.Module("NativeAppInfoModule") {
        bind<AppInfoRepository> {
            singleton {
                DefaultAppInfoRepository(context = instance())
            }
        }
    }
