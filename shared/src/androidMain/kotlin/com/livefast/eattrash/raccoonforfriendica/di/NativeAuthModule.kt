package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.auth.DefaultRedirectServer
import com.livefast.eattrash.raccoonforfriendica.auth.RedirectServer
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

actual val nativeAuthModule = DI.Module("NativeAuthModule") {
    bind<RedirectServer> {
        singleton {
            DefaultRedirectServer()
        }
    }
}
