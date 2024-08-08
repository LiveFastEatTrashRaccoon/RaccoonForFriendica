package com.livefast.eattrash.raccoonforfriendica.feature.inbox.di

import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxViewModel
import org.koin.dsl.module

val featureInboxModule =
    module {
        factory<InboxMviModel> {
            InboxViewModel(
                paginationManager = get(),
            )
        }
    }
