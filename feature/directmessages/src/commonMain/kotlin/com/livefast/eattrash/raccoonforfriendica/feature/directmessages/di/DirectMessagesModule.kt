package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.di

import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.DirectMessageListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.DirectMessageListViewModel
import org.koin.dsl.module

val featureDirectMessagesModule =
    module {
        factory<DirectMessageListMviModel> {
            DirectMessageListViewModel(
                paginationManager = get(),
                identityRepository = get(),
                userPaginationManager = get(),
            )
        }
    }
