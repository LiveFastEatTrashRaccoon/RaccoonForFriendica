package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.directmessages")
internal class DirectMessagesModule

val featureDirectMessagesModule = DirectMessagesModule().module
