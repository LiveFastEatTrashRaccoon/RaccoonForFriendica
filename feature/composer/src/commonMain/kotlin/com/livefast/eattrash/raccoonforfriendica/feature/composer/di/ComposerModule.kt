package com.livefast.eattrash.raccoonforfriendica.feature.composer.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.composer")
internal class ComposerModule

val featureComposerModule = ComposerModule().module
