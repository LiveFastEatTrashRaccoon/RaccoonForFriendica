package com.livefast.eattrash.raccoonforfriendica.feature.thread.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.thread")
internal class ThreadModule

val featureThreadModule = ThreadModule().module
