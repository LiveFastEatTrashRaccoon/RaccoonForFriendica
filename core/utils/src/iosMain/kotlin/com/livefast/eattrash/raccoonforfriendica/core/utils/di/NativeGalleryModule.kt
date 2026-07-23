package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.DefaultGalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import org.koin.dsl.module

internal actual val nativeGalleryModule = module {
    single<GalleryHelper> {
        DefaultGalleryHelper()
    }
}
