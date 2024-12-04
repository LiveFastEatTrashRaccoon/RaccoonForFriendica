package com.livefast.eattrash.raccoonforfriendica.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.main")
internal class MainModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.navigation")
internal class NavigationModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.auth")
internal class AuthModule

@Module(includes = [AuthModule::class, NavigationModule::class, MainModule::class])
internal class SharedModule

internal val sharedModule = SharedModule().module
