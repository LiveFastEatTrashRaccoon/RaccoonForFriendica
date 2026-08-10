package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [CacheModule::class])
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.content.repository")
class ContentRepositoryModule
