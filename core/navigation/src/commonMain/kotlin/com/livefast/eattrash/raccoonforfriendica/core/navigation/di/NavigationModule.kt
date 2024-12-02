package com.livefast.eattrash.raccoonforfriendica.core.navigation.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.navigation")
internal class NavigationModule

val coreNavigationModule = NavigationModule().module
