package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.di

import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationViewModel
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
        factory<ConversationMviModel> { params ->
            ConversationViewModel(
                otherUserId = params[0],
                parentUri = params[1],
                paginationManager = get(),
                userRepository = get(),
                identityRepository = get(),
                messageRepository = get(),
            )
        }
    }
