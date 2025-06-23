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
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.singleton

val urlHandlerModule =
    DI.Module("UrlHandlerModule") {
        bind<FetchUserUseCase> {
            singleton {
                DefaultFetchUserUseCase(
                    searchRepository = instance(),
                )
            }
        }
        bind<FetchEntryUseCase> {
            singleton {
                DefaultFetchEntryUseCase(
                    searchRepository = instance(),
                )
            }
        }
        bind<HashtagProcessor> {
            singleton {
                DefaultHashtagProcessor(
                    mainRouter = instance(),
                )
            }
        }
        bind<UserProcessor> {
            singleton {
                DefaultUserProcessor(
                    mainRouter = instance(),
                    fetchUser = instance(),
                )
            }
        }
        bind<EntryProcessor> {
            singleton {
                DefaultEntryProcessor(
                    mainRouter = instance(),
                    fetchEntry = instance(),
                )
            }
        }

        bind<CustomUriHandler> {
            factory { fallback: UriHandler ->
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
    }
