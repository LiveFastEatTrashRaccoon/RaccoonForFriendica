package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.impl")
internal class NativePushNotificationsModule

internal actual val nativePushNotificationsModule = NativePushNotificationsModule().module
