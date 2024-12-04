package com.livefast.eattrash.raccoonforfriendica.core.notifications.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.notifications")
internal class NotificationsModule

val coreNotificationsModule = NotificationsModule().module
