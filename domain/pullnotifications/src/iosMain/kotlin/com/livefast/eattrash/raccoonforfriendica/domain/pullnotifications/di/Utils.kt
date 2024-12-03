package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.DefaultPullNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications")
internal class NativePullNotificationsModule

internal actual val nativePullNotificationsModule =
    module {
        single<PullNotificationManager> {
            DefaultPullNotificationManager()
        }
    }
