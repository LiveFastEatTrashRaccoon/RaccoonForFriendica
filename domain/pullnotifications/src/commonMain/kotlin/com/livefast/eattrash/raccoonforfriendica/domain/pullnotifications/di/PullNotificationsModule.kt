package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.di

import org.kodein.di.DI

val pullNotificationsModule =
    DI.Module("PullNotificationsModule") {
        import(nativePullNotificationsModule)
    }
