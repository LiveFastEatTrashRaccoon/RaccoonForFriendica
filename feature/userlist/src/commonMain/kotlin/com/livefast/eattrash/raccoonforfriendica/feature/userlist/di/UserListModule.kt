package com.livefast.eattrash.raccoonforfriendica.feature.userlist.di

import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListViewModel
import org.koin.dsl.module

val featureUserListModule =
    module {
        factory<UserListMviModel> { params ->
            UserListViewModel(
                type = params[0],
                userId = params[1],
                entryId = params[2],
                paginationManager = get(),
                userRepository = get(),
                hapticFeedback = get(),
                notificationCenter = get(),
            )
        }
    }
