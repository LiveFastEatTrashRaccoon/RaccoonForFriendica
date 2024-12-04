package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.coordinator")
internal class CoordinatorModule

val domainPushNotificationsModule =
    module {
        includes(
            ManagerModule().module,
            CoordinatorModule().module,
        )
    }
