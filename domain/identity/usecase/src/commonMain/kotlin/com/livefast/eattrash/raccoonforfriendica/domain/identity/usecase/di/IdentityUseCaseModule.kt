package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase")
internal class IdentityUseCaseModule

val domainIdentityUseCaseModule = IdentityUseCaseModule().module
