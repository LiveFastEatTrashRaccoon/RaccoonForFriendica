package com.livefast.eattrash.raccoonforfriendica.feature.userlist.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

data class UserListMviModelParams(
    val type: UserListType,
    val userId: String,
    val entryId: String,
)

val userListModule =
    DI.Module("UserListModule") {
        bind<UserListMviModel> {
            factory { params: UserListMviModelParams ->
                UserListViewModel(
                    type = params.type,
                    userId = params.userId,
                    entryId = params.entryId,
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
    }
