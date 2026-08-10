package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.auth.DefaultAuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.plugin.module.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

@Module(includes = [NativeAuthModule::class])
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.auth")
class AuthModule
