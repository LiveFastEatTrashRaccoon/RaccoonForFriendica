package com.livefast.eattrash.raccoonforfriendica.feature.drawer.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.drawer")
internal class DrawerModule

val featureDrawerModule = DrawerModule().module
