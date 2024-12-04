package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.appearance.repository")
internal class RepositoryModule

@Module(includes = [ThemeModule::class, RepositoryModule::class])
internal class AppearanceModule

val coreAppearanceModule = AppearanceModule().module
