package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.DefaultSetupAccountUseCase
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
    }
