package com.livefast.eattrash.raccoonforfriendica.feature.userlist.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class UserListViewModelArgs(val type: UserListType, val userId: String, val entryId: String) :
    ViewModelCreationArgs

val userListModule =
    DI.Module("UserListModule") {
        bindViewModelWithArgs { args: UserListViewModelArgs ->
            UserListViewModel(
                type = args.type,
                userId = args.userId,
                entryId = args.entryId,
                paginationManager = instance(),
                userRepository = instance(),
                identityRepository = instance(),
                settingsRepository = instance(),
                hapticFeedback = instance(),
                imagePreloadManager = instance(),
                userCache = instance(),
                imageAutoloadObserver = instance(),
                exportUserList = instance(),
                notificationCenter = instance(),
            )
        }
    }
