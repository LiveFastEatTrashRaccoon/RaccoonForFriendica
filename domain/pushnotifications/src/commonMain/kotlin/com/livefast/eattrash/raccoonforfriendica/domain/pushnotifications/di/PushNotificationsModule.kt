package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [NativePushNotificationsModule::class])
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications")
class PushNotificationsModule
