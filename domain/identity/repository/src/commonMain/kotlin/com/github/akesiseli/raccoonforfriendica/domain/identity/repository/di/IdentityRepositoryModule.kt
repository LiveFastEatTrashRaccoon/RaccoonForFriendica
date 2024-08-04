package com.github.akesiseli.raccoonforfriendica.domain.identity.repository.di

import com.github.akesiseli.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.github.akesiseli.raccoonforfriendica.domain.identity.repository.DefaultApiConfigurationRepository
import org.koin.dsl.module

val domainIdentityRepositoryModule =
    module {
        single<ApiConfigurationRepository> {
            DefaultApiConfigurationRepository(
                serviceProvider = get(),
                keyStore = get(),
            )
        }
    }
