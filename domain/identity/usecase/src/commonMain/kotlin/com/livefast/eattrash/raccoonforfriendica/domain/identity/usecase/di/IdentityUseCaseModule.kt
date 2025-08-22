package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ContentPreloadManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultContentPreloadManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultDeleteAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultEntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultExportSettingsUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultImportSettingsUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultLoginUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultLogoutUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultSetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultSwitchAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DeleteAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ExportSettingsUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ImportSettingsUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LoginUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LogoutUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SwitchAccountUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val identityUseCaseModule =
    DI.Module("IdentityUseCaseModule") {
        bind<ActiveAccountMonitor> {
            singleton {
                DefaultActiveAccountMonitor(
                    accountRepository = instance(),
                    apiConfigurationRepository = instance(),
                    identityRepository = instance(),
                    accountCredentialsCache = instance(),
                    settingsRepository = instance(),
                    supportedFeatureRepository = instance(),
                    contentPreloadManager = instance(),
                    markerRepository = instance(),
                    notificationCoordinator = instance(),
                    announcementsManager = instance(),
                    followedHashtagCache = instance(),
                    serviceProvider = instance(tag = "default"),
                    logout = instance(),
                )
            }
        }
        bind<ContentPreloadManager> {
            singleton {
                DefaultContentPreloadManager(
                    timelineEntryRepository = instance(),
                    trendingRepository = instance(),
                    notificationRepository = instance(),
                )
            }
        }
        bind<DeleteAccountUseCase> {
            singleton {
                DefaultDeleteAccountUseCase(
                    accountRepository = instance(),
                    settingsRepository = instance(),
                    accountCredentialsCache = instance(),
                )
            }
        }
        bind<EntryActionRepository> {
            singleton {
                DefaultEntryActionRepository(
                    identityRepository = instance(),
                    supportedFeatureRepository = instance(),
                )
            }
        }
        bind<ExportSettingsUseCase> {
            singleton {
                DefaultExportSettingsUseCase(
                    settingsRepository = instance(),
                )
            }
        }
        bind<ImportSettingsUseCase> {
            singleton {
                DefaultImportSettingsUseCase(
                    settingsRepository = instance(),
                )
            }
        }
        bind<LoginUseCase> {
            singleton {
                DefaultLoginUseCase(
                    apiConfigurationRepository = instance(),
                    credentialsRepository = instance(),
                    accountRepository = instance(),
                    settingsRepository = instance(),
                    accountCredentialsCache = instance(),
                    supportedFeatureRepository = instance(),
                )
            }
        }
        bind<LogoutUseCase> {
            singleton {
                DefaultLogoutUseCase(
                    apiConfigurationRepository = instance(),
                    accountRepository = instance(),
                )
            }
        }
        bind<SetupAccountUseCase> {
            singleton {
                DefaultSetupAccountUseCase(
                    accountRepository = instance(),
                    settingsRepository = instance(),
                )
            }
        }
        bind<SwitchAccountUseCase> {
            singleton {
                DefaultSwitchAccountUseCase(
                    accountRepository = instance(),
                )
            }
        }
    }
