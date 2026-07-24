package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.di

import org.koin.dsl.module

val pullNotificationsModule = module {
    includes(nativePullNotificationsModule)
}
