package com.livefast.eattrash.raccoonforfriendica.feature.gallery.di

import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

val galleryModule =
    DI.Module("GalleryModule") {
        bind<GalleryMviModel> {
            provider {
                GalleryViewModel(
                    albumRepository = instance(),
                    settingsRepository = instance(),
                    notificationCenter = instance(),
                )
            }
        }
        bind<AlbumDetailMviModel> {
            factory { albumName: String ->
                AlbumDetailViewModel(
                    albumName = albumName,
                    paginationManager = instance(),
                    photoRepository = instance(),
                    albumRepository = instance(),
                    settingsRepository = instance(),
                    imageAutoloadObserver = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
