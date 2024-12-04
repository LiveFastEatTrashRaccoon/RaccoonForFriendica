package com.livefast.eattrash.raccoonforfriendica.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.resources")
internal class ResourceModule

internal val coreResourceModule = ResourceModule().module
