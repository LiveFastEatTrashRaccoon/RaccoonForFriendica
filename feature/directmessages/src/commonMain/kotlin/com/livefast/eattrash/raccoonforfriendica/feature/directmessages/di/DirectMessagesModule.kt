package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.di

import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal data class ConversationViewModelArgs(val otherUserId: String, val parentUri: String)

val directMessagesModule = module {
    viewModel {
        ConversationListViewModel(
            paginationManager = get(),
            identityRepository = get(),
            settingsRepository = get(),
            userPaginationManager = get(),
            imageAutoloadObserver = get(),
        )
    }
    viewModel { params ->
        val args: ConversationViewModelArgs = params.get()
        ConversationViewModel(
            otherUserId = args.otherUserId,
            parentUri = args.parentUri,
            paginationManager = get(),
            identityRepository = get(),
            userRepository = get(),
            messageRepository = get(),
            userCache = get(),
            imageAutoloadObserver = get(),
        )
    }
}
