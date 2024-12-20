package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.DefaultGalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal actual val nativeGalleryModule =
    DI.Module("NativeGalleryModule") {
        bind<GalleryHelper> {
            singleton {
                DefaultGalleryHelper(
                    context = instance(),
                )
            }
        }
    }
