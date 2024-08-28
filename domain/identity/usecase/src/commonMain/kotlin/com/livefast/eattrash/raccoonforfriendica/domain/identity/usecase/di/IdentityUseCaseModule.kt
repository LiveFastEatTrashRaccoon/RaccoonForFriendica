package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.CustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultCustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultLoginUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultLogoutUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultSetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LoginUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LogoutUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
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
                identityRepository = get(),
                accountCredentialsCache = get(),
            )
        }

        single<LogoutUseCase> {
            DefaultLogoutUseCase(
                apiConfigurationRepository = get(),
                accountRepository = get(),
                settingsRepository = get(),
            )
        }
        single<CustomUriHandler> { params ->
            DefaultCustomUriHandler(
                defaultHandler = params[0],
                apiConfigurationRepository = get(),
                detailOpener = get(),
                userRepository = get(),
                customTabsHelper = get(),
                settingsRepository = get(),
            )
        }
    }
