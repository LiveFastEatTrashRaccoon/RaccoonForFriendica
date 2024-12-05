package com.livefast.eattrash.raccoonforfriendica.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.main")
internal class MainModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.navigation")
internal class DetailOpenModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.auth")
internal class AuthModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.resources")
internal class ResourcesModule

@Module(includes = [AuthModule::class, DetailOpenModule::class, ResourcesModule::class, MainModule::class])
internal class SharedModule
