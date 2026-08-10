package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class ShortcutTimelineViewModelArgs(val name: String)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.shortcuts")
class ShortcutsModule
