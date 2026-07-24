package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.auth.DefaultRedirectServer
import com.livefast.eattrash.raccoonforfriendica.auth.EmbeddedRedirectServer
import com.livefast.eattrash.raccoonforfriendica.auth.RedirectServer
import org.koin.dsl.module

actual val nativeAuthModule = module {
    single {
        EmbeddedRedirectServer()
    }
    single<RedirectServer> {
        DefaultRedirectServer(server = get())
    }
}
