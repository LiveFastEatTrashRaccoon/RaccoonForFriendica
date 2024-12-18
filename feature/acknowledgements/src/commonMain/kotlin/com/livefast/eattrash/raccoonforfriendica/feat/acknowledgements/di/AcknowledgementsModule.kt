package com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource")
internal class DataSourceModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.repository")
internal class RepositoryModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.main")
internal class MainModule

@Module(includes = [DataSourceModule::class, RepositoryModule::class, MainModule::class])
class AcknowledgementsModule
