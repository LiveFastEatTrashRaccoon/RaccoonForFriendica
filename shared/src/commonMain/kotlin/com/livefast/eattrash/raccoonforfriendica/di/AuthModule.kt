package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.auth.DefaultAuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import org.koin.dsl.module

internal val authModule = module {
    includes(nativeAuthModule)

    single<AuthManager> {
        DefaultAuthManager(
            navigationCoordinator = get(),
            credentialsRepository = get(),
            keyStore = get(),
            redirectServer = get(),
        )
    }
}
