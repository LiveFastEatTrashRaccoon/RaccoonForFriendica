package com.livefast.eattrash.raccoonforfriendica.feature.entrylist.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EntryListType
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class EntryListViewModelArgs(val type: EntryListType)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.entrylist")
class EntryListModule
