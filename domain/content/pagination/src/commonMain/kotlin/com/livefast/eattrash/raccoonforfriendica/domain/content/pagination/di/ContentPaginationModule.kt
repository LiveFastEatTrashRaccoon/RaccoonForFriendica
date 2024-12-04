package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.content.pagination")
internal class ContentPaginationModule

val domainContentPaginationModule = ContentPaginationModule().module
