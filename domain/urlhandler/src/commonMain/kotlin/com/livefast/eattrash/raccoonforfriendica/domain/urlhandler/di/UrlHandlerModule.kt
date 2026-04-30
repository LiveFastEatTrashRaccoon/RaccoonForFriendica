package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.DefaultCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultEntryProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultFetchEntryUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultFetchUserUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultHashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.EntryProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.FetchEntryUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.FetchUserUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.HashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UserProcessor
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val urlHandlerModule =
    DI.Module("UrlHandlerModule") {
        bindSingleton<FetchUserUseCase> {
            DefaultFetchUserUseCase(
                searchRepository = instance(),
            )
        }
        bindSingleton<FetchEntryUseCase> {
            DefaultFetchEntryUseCase(
                searchRepository = instance(),
            )
        }
        bindSingleton<HashtagProcessor> {
            DefaultHashtagProcessor(
                mainRouter = instance(),
            )
        }
        bindSingleton<UserProcessor> {
            DefaultUserProcessor(
                mainRouter = instance(),
                fetchUser = instance(),
            )
        }
        bindSingleton<EntryProcessor> {
            DefaultEntryProcessor(
                mainRouter = instance(),
                fetchEntry = instance(),
            )
        }

        bindFactory<UriHandler, CustomUriHandler> { fallback ->
            DefaultCustomUriHandler(
                defaultHandler = fallback,
                customTabsHelper = instance(),
                settingsRepository = instance(),
                mainRouter = instance(),
                hashtagProcessor = instance(),
                userProcessor = instance(),
                entryProcessor = instance(),
            )
        }
    }
