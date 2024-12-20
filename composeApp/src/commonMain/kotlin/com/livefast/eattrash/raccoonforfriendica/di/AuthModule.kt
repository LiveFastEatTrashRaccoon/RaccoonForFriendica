package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.auth.DefaultAuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal val authModule =
    DI.Module("AuthModule") {
        bind<AuthManager> {
            singleton {
                DefaultAuthManager(
                    navigationCoordinator = instance(),
                    credentialsRepository = instance(),
                    keyStore = instance(),
                )
            }
        }
    }
