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
import org.koin.dsl.module

val urlHandlerModule = module {
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
            mainRouter = get(),
        )
    }
    single<UserProcessor> {
        DefaultUserProcessor(
            mainRouter = get(),
            fetchUser = get(),
        )
    }
    single<EntryProcessor> {
        DefaultEntryProcessor(
            mainRouter = get(),
            fetchEntry = get(),
        )
    }

    factory<CustomUriHandler> { params ->
        val arg: UriHandler = params.get()
        DefaultCustomUriHandler(
            defaultHandler = arg,
            customTabsHelper = get(),
            settingsRepository = get(),
            mainRouter = get(),
            hashtagProcessor = get(),
            userProcessor = get(),
            entryProcessor = get(),
        )
    }
}
