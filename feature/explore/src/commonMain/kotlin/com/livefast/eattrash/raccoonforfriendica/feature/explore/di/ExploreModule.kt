package com.livefast.eattrash.raccoonforfriendica.feature.explore.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.explore")
internal class ExploreModule

val featureExploreModule = ExploreModule().module
