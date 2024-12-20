package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.DefaultCrashReportManager
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal actual val nativeDebugModule =
    DI.Module("NativeDebugModule") {
        bind<CrashReportManager> {
            singleton {
                DefaultCrashReportManager(
                    keyStore = instance(),
                )
            }
        }
    }
