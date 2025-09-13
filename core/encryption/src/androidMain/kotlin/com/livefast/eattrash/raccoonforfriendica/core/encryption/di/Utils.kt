package com.livefast.eattrash.raccoonforfriendica.core.encryption.di

import com.livefast.eattrash.raccoonforfriendica.core.encryption.DefaultSecureKeyStorage
import com.livefast.eattrash.raccoonforfriendica.core.encryption.SecureKeyStorage
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

actual val nativeEncryptionModule = DI.Module("NativeEncryptionModule") {
    bind<SecureKeyStorage> {
        singleton {
            DefaultSecureKeyStorage(
                context = instance(),
            )
        }
    }
}
