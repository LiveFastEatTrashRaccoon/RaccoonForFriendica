package com.livefast.eattrash.raccoonforfriendica.feature.gallery.di

import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryViewModel
import org.koin.dsl.module

val featureGalleryModule =
    module {
        factory<GalleryMviModel> {
            GalleryViewModel(
                albumRepository = get(),
                notificationCenter = get(),
            )
        }
        factory<AlbumDetailMviModel> { params ->
            AlbumDetailViewModel(
                albumName = params[0],
                paginationManager = get(),
                photoRepository = get(),
            )
        }
    }
