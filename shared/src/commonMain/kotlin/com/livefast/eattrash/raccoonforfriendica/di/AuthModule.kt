package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.auth.DefaultAuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal val authModule =
    DI.Module("AuthModule") {
        import(nativeAuthModule)

        bindSingleton<AuthManager> {
            DefaultAuthManager(
                navigationCoordinator = instance(),
                credentialsRepository = instance(),
                keyStore = instance(),
                redirectServer = instance(),
            )
        }
    }
