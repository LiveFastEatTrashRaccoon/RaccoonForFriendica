package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.appicon.AppIconManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.appicon.DefaultAppIconManager
import org.koin.dsl.module

internal actual val nativeAppIconModule = module {
    single<AppIconManager> {
        DefaultAppIconManager()
    }
}
