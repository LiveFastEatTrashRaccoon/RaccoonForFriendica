package com.livefast.eattrash.raccoonforfriendica.feature.gallery.di

import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryViewModel
import org.koin.dsl.module

val featureGalleryModule =
    module {
        factory<GalleryMviModel> {
            GalleryViewModel(
                albumRepository = get(),
            )
        }
    }
