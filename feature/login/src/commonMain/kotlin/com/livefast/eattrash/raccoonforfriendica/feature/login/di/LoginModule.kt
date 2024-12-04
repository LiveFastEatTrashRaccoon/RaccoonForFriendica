package com.livefast.eattrash.raccoonforfriendica.feature.login.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.login")
internal class LoginModule

val featureLoginModule = LoginModule().module
