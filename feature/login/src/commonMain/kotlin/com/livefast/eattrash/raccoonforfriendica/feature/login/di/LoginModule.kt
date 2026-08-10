package com.livefast.eattrash.raccoonforfriendica.feature.login.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.LoginType
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class LoginViewModelArgs(val type: LoginType)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.login")
class LoginModule
