package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.DefaultAppInfoRepository
import org.koin.dsl.module

internal actual val nativeAppInfoModule = module {
    single<AppInfoRepository> {
        DefaultAppInfoRepository()
    }
}
