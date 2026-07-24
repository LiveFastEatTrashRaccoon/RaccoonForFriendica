package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common.DefaultUnifiedPushInteractor
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common.UnifiedPushInteractor
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.DefaultPushNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.PushNotificationManager
import org.koin.dsl.module

internal actual val nativePushNotificationsModule = module {
    single<PushNotificationManager> {
        DefaultPushNotificationManager(
            context = get(),
            pushNotificationRepository = get(),
            accountRepository = get(),
            nodeInfoRepository = get(),
        )
    }
    single<UnifiedPushInteractor> {
        DefaultUnifiedPushInteractor(
            pullNotificationManager = get(),
            pushNotificationManager = get(),
            accountRepository = get(),
        )
    }
}
