package com.livefast.eattrash.raccoonforfriendica.feature.userlist.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class UserListViewModelArgs(val type: UserListType, val userId: String, val entryId: String)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.userlist")
class UserListModule
