package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common.DefaultUnifiedPushInteractor
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common.UnifiedPushInteractor
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.DefaultPushNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.PushNotificationManager
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal actual val nativePushNotificationsModule =
    DI.Module("NativePushNotificationsModule") {
        bindSingleton<PushNotificationManager> {
            DefaultPushNotificationManager(
                context = instance(),
                pushNotificationRepository = instance(),
                accountRepository = instance(),
                nodeInfoRepository = instance(),
            )
        }
        bindSingleton<UnifiedPushInteractor> {
            DefaultUnifiedPushInteractor(
                pullNotificationManager = instance(),
                pushNotificationManager = instance(),
                accountRepository = instance(),
            )
        }
    }
