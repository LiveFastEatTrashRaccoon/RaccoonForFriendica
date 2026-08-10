package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class EntryDetailViewModelArgs(val id: String, val swipeNavigationEnabled: Boolean)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.entrydetail")
class EntryDetailModule
