package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.manageblocks")
internal class ManageBlocksModule

val featureManageBlocksModule = ManageBlocksModule().module
