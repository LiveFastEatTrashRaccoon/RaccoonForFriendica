package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.coordinator")
internal class CoordinatorModule

@Module(includes = [ManagerModule::class, CoordinatorModule::class])
internal class PushNotificationsModule

val domainPushNotificationsModule = PushNotificationsModule().module
