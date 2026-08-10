package com.livefast.eattrash.raccoonforfriendica.feature.thread.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class ThreadViewModelArgs(val entryId: String, val swipeNavigationEnabled: Boolean)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.thread")
class ThreadModule
