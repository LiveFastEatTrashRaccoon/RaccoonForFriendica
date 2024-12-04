package com.livefast.eattrash.raccoonforfriendica.feature.profile.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.profile")
internal class ProfileModule

val featureProfileModule = ProfileModule().module
