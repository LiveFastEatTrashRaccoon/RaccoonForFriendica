package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import org.kodein.di.DI

internal actual val nativeImageLoadModule = DI.Module("NativeImageLoadModule") {
    // on Android nothing is needed
}
