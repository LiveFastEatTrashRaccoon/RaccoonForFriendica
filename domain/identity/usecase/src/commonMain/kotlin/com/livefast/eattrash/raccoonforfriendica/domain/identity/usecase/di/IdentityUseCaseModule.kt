package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ContentPreloadManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.CustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultContentPreloadManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultDeleteAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultEntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultLoginUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultLogoutUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultSetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultSwitchAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DeleteAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LoginUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LogoutUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SwitchAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.DefaultExternalUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.DefaultFetchUserUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.DefaultFriendicaUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.DefaultGuppeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.DefaultHashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.DefaultLemmyProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.DefaultMastodonUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.DefaultPeertubeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.DefaultPixelfedProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.ExternalUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.FetchUserUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.FriendicaUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.GuppeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.HashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.LemmyProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.MastodonUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.PeertubeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.PixelfedProcessor
import org.koin.dsl.module

val domainIdentityUseCaseModule =
    module {
        single<SetupAccountUseCase> {
            DefaultSetupAccountUseCase(
                accountRepository = get(),
                settingsRepository = get(),
            )
        }

        single<LoginUseCase> {
            DefaultLoginUseCase(
                credentialsRepository = get(),
                apiConfigurationRepository = get(),
                accountRepository = get(),
                settingsRepository = get(),
                accountCredentialsCache = get(),
                supportedFeatureRepository = get(),
            )
        }

        single<LogoutUseCase> {
            DefaultLogoutUseCase(
                apiConfigurationRepository = get(),
                accountRepository = get(),
            )
        }
        single<FetchUserUseCase> {
            DefaultFetchUserUseCase(
                userRepository = get(),
            )
        }
        single<HashtagProcessor> {
            DefaultHashtagProcessor(
                apiConfigurationRepository = get(),
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
            )
        }
        single<SwitchAccountUseCase> {
            DefaultSwitchAccountUseCase(
                accountRepository = get(),
            )
        }
        single<DeleteAccountUseCase> {
            DefaultDeleteAccountUseCase(
                accountRepository = get(),
                settingsRepository = get(),
                accountCredentialsCache = get(),
            )
        }
        single<ActiveAccountMonitor> {
            DefaultActiveAccountMonitor(
                accountRepository = get(),
                apiConfigurationRepository = get(),
                identityRepository = get(),
                settingsRepository = get(),
                accountCredentialsCache = get(),
                supportedFeatureRepository = get(),
                inboxManager = get(),
                contentPreloadManager = get(),
                markerRepository = get(),
            )
        }
        single<EntryActionRepository> {
            DefaultEntryActionRepository(
                identityRepository = get(),
                supportedFeatureRepository = get(),
            )
        }
        single<ContentPreloadManager> {
            DefaultContentPreloadManager(
                timelineEntryRepository = get(),
                trendingRepository = get(),
                notificationRepository = get(),
            )
        }
    }
