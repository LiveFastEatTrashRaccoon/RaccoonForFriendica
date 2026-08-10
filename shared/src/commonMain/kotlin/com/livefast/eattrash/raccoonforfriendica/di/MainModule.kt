package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.main.MainViewModel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.main")
class MainModule
