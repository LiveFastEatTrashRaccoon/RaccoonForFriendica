package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.di

import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.DirectMessageListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.DirectMessageListViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

internal data class ConversationMviModelParams(
    val otherUserId: String,
    val parentUri: String,
)

val directMessagesModule =
    DI.Module("DirectMessagesModule") {
        bind<DirectMessageListMviModel> {
            provider {
                DirectMessageListViewModel(
                    paginationManager = instance(),
                    identityRepository = instance(),
                    settingsRepository = instance(),
                    userPaginationManager = instance(),
                    imageAutoloadObserver = instance(),
                )
            }
        }
        bind<ConversationMviModel> {
            factory { params: ConversationMviModelParams ->
                ConversationViewModel(
                    otherUserId = params.otherUserId,
                    parentUri = params.parentUri,
                    paginationManager = instance(),
                    identityRepository = instance(),
                    userRepository = instance(),
                    messageRepository = instance(),
                    userCache = instance(),
                    imageAutoloadObserver = instance(),
                )
            }
        }
    }
