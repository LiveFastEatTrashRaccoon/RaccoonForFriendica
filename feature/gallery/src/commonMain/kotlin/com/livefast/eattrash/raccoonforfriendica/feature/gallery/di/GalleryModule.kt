package com.livefast.eattrash.raccoonforfriendica.feature.gallery.di

import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class AlbumDetailViewModelArgs(val albumName: String)

val galleryModule = module {
    viewModel {
        GalleryViewModel(
            albumRepository = get(),
            settingsRepository = get(),
            notificationCenter = get(),
        )
    }
    viewModel { params ->
        val args: AlbumDetailViewModelArgs = params.get()
        AlbumDetailViewModel(
            albumName = args.albumName,
            paginationManager = get(),
            photoRepository = get(),
            albumRepository = get(),
            settingsRepository = get(),
            imageAutoloadObserver = get(),
            notificationCenter = get(),
        )
    }
}
