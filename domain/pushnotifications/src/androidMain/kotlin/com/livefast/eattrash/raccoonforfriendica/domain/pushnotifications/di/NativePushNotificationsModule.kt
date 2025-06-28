package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common.DefaultUnifiedPushInteractor
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common.UnifiedPushInteractor
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.DefaultPushNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.PushNotificationManager
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal actual val nativePushNotificationsModule =
    DI.Module("NativePushNotificationsModule") {
        bind<PushNotificationManager> {
            singleton {
                DefaultPushNotificationManager(
                    context = instance(),
                    pushNotificationRepository = instance(),
                    accountRepository = instance(),
                )
            }
        }
        bind<UnifiedPushInteractor> {
            singleton {
                DefaultUnifiedPushInteractor(
                    pullNotificationManager = instance(),
                    pushNotificationManager = instance(),
                    accountRepository = instance(),
                )
            }
        }
    }
