package com.livefast.eattrash.raccoonforfriendica.core.encryption.di

import com.livefast.eattrash.raccoonforfriendica.core.encryption.DefaultEncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.encryption.EncryptionHelper
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val encryptionModule = DI.Module("EncryptionModule") {
    import(nativeEncryptionModule)
    bind<EncryptionHelper> {
        singleton {
            DefaultEncryptionHelper(
                storage = instance(),
            )
        }
    }
}
