package com.livefast.eattrash.raccoonforfriendica.feature.userlist.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class UserListViewModelArgs(val type: UserListType, val userId: String, val entryId: String)

val userListModule = module {
    viewModel { params ->
        val args: UserListViewModelArgs = params.get()
        UserListViewModel(
            type = args.type,
            userId = args.userId,
            entryId = args.entryId,
            paginationManager = get(),
            userRepository = get(),
            identityRepository = get(),
            settingsRepository = get(),
            hapticFeedback = get(),
            imagePreloadManager = get(),
            userCache = get(),
            imageAutoloadObserver = get(),
            exportUserList = get(),
            notificationCenter = get(),
        )
    }
}
