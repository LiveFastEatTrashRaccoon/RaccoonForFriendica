package com.livefast.eattrash.raccoonforfriendica.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [NativeAuthModule::class])
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.auth")
class AuthModule
