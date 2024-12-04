package com.livefast.eattrash.raccoonforfriendica.feature.inbox.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.inbox")
internal class InboxModule

val featureInboxModule = InboxModule().module
