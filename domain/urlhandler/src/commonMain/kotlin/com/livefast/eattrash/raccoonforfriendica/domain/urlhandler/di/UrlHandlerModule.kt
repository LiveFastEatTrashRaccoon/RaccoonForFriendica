package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di

import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.CustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.DefaultCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultExternalUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultFetchUserUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultFriendicaUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultGuppeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultHashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultLemmyProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultMastodonUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultPeertubeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.DefaultPixelfedProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.ExternalUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.FetchUserUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.FriendicaUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.GuppeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.HashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.LemmyProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.MastodonUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.PeertubeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.PixelfedProcessor
import org.koin.dsl.module

val domainUrlHandlerModule =
    module {
        single<FetchUserUseCase> {
            DefaultFetchUserUseCase(
                userRepository = get(),
                apiConfigurationRepository = get(),
            )
        }
        single<HashtagProcessor> {
            DefaultHashtagProcessor(
                detailOpener = get(),
            )
        }
        single<FriendicaUserProcessor> {
            DefaultFriendicaUserProcessor(
                detailOpener = get(),
                fetchUser = get(),
            )
        }
        single<ExternalUserProcessor> {
            DefaultExternalUserProcessor(
                detailOpener = get(),
                fetchUser = get(),
            )
        }
        single<MastodonUserProcessor> {
            DefaultMastodonUserProcessor(
                detailOpener = get(),
                fetchUser = get(),
            )
        }
        single<PixelfedProcessor> {
            DefaultPixelfedProcessor(
                detailOpener = get(),
                fetchUser = get(),
            )
        }
        single<LemmyProcessor> {
            DefaultLemmyProcessor(
                detailOpener = get(),
                fetchUser = get(),
            )
        }
        single<GuppeProcessor> {
            DefaultGuppeProcessor(
                detailOpener = get(),
                fetchUser = get(),
            )
        }
        single<PeertubeProcessor> {
            DefaultPeertubeProcessor(
                detailOpener = get(),
                fetchUser = get(),
            )
        }
        single<CustomUriHandler> { params ->
            DefaultCustomUriHandler(
                defaultHandler = params[0],
                customTabsHelper = get(),
                settingsRepository = get(),
                hashtagProcessor = get(),
                friendicaUserProcessor = get(),
                externalUserProcessor = get(),
                mastodonUserProcessor = get(),
                lemmyProcessor = get(),
                guppeProcessor = get(),
                peertubeProcessor = get(),
                pixelfedProcessor = get(),
                detailOpener = get(),
            )
        }
    }
