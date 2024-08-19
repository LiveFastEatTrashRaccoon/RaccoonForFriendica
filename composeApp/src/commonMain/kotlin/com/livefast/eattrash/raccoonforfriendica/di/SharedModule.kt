package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.auth.DefaultAuthManager
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.main.MainMviModel
import com.livefast.eattrash.raccoonforfriendica.main.MainViewModel
import com.livefast.eattrash.raccoonforfriendica.navigation.DefaultDetailOpener
import org.koin.dsl.module

internal val sharedModule =
    module {
        factory<MainMviModel> {
            MainViewModel()
        }
        single<DetailOpener> {
            DefaultDetailOpener(
                navigationCoordinator = get(),
                identityRepository = get(),
            )
        }
        single<AuthManager> {
            DefaultAuthManager(
                navigationCoordinator = get(),
                credentialsRepository = get(),
                keyStore = get(),
            )
        }
    }
