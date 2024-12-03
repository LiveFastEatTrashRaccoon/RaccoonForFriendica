package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.identity.repository")
internal class IdentityRepositoryModule

val domainIdentityRepositoryModule = IdentityRepositoryModule().module
