package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor")
internal class UrlHandlerProcessorModule

@Module(includes = [UrlHandlerProcessorModule::class])
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.urlhandler")
internal class UrlHandlerModule

val domainUrlHandlerModule = UrlHandlerModule().module
