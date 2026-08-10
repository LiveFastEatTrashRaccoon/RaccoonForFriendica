package com.livefast.eattrash.raccoonforfriendica.feature.userdetail.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class UserDetailViewModelArgs(val id: String)

data class ForumListViewModelArgs(val id: String)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.userdetail")
class UserDetailModule
