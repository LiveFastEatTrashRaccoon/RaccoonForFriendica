package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.PushNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.impl.DefaultPushNotificationManager
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.impl")
internal class NativePushNotificationsModule

actual val nativePushNotificationsModule =
    module {
        single<PushNotificationManager> {
            DefaultPushNotificationManager()
        }
    }
