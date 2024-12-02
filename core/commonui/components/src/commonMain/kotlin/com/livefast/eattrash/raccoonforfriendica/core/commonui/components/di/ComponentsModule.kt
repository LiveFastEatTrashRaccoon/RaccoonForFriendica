package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.commonui.components")
internal class CommonUiComponentsModule

val coreCommonUiComponentsModule = CommonUiComponentsModule().module
