package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di

import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.DefaultCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultEntryProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultFetchEntryUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultFetchUserUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultHashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.EntryProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.FetchEntryUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.FetchUserUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.HashtagProcessor
import org.koin.dsl.module

val domainUrlHandlerModule =
    module {
        single<FetchUserUseCase> {
            DefaultFetchUserUseCase(
                searchRepository = get(),
            )
        }
        single<FetchEntryUseCase> {
            DefaultFetchEntryUseCase(
                searchRepository = get(),
            )
        }
        single<HashtagProcessor> {
            DefaultHashtagProcessor(
                detailOpener = get(),
            )
        }
        single<EntryProcessor> {
            DefaultEntryProcessor(
                detailOpener = get(),
                fetchEntry = get(),
            )
        }
        single<CustomUriHandler> { params ->
            DefaultCustomUriHandler(
                defaultHandler = params[0],
                customTabsHelper = get(),
                settingsRepository = get(),
                detailOpener = get(),
                hashtagProcessor = get(),
                userProcessor = get(),
                entryProcessor = get(),
            )
        }
    }
