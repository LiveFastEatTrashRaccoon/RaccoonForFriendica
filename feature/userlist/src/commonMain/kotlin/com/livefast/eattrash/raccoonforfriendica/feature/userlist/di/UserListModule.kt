package com.livefast.eattrash.raccoonforfriendica.feature.userlist.di

import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListViewModel
import org.koin.dsl.module

val featureUserListModule =
    module {
        factory<UserListMviModel> { params ->
            UserListViewModel(
                userId = params[0],
                type = params[1],
                paginationManager = get(),
                userRepository = get(),
            )
        }
    }
