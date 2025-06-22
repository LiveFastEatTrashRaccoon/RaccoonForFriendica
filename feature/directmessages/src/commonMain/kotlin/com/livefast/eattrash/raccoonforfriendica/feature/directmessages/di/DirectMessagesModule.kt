package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListViewModel
import org.kodein.di.DI
import org.kodein.di.instance

internal data class ConversationViewModelArgs(val otherUserId: String, val parentUri: String) : ViewModelCreationArgs

val directMessagesModule =
    DI.Module("DirectMessagesModule") {
        bindViewModel {
            ConversationListViewModel(
                paginationManager = instance(),
                identityRepository = instance(),
                settingsRepository = instance(),
                userPaginationManager = instance(),
                imageAutoloadObserver = instance(),
            )
        }
        bindViewModelWithArgs { args: ConversationViewModelArgs ->
            ConversationViewModel(
                otherUserId = args.otherUserId,
                parentUri = args.parentUri,
                paginationManager = instance(),
                identityRepository = instance(),
                userRepository = instance(),
                messageRepository = instance(),
                userCache = instance(),
                imageAutoloadObserver = instance(),
            )
        }
    }
