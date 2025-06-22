package com.livefast.eattrash.raccoonforfriendica.feature.gallery.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class AlbumDetailViewModelArgs(val albumName: String) : ViewModelCreationArgs

val galleryModule =
    DI.Module("GalleryModule") {
        bindViewModel {
            GalleryViewModel(
                albumRepository = instance(),
                settingsRepository = instance(),
                notificationCenter = instance(),
            )
        }
        bindViewModelWithArgs { args: AlbumDetailViewModelArgs ->
            AlbumDetailViewModel(
                albumName = args.albumName,
                paginationManager = instance(),
                photoRepository = instance(),
                albumRepository = instance(),
                settingsRepository = instance(),
                imageAutoloadObserver = instance(),
                notificationCenter = instance(),
            )
        }
    }
