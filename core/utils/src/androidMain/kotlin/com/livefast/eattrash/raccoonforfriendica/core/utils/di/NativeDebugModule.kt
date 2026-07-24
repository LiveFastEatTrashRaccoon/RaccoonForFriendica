package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.DefaultCrashReportManager
import org.koin.dsl.module

internal actual val nativeDebugModule = module {
    single<CrashReportManager> {
        DefaultCrashReportManager(
            keyStore = get(),
        )
    }
}
