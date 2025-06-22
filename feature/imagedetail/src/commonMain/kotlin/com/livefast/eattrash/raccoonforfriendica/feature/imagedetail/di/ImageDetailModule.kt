package com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailViewModel
import org.kodein.di.DI
import org.kodein.di.instance

internal data class ImageDetailViewModelArgs(val urls: List<String>, val initialIndex: Int = 0) : ViewModelCreationArgs

val imageDetailModule =
    DI.Module("ImageDetailModule") {
        bindViewModelWithArgs { args: ImageDetailViewModelArgs ->
            ImageDetailViewModel(
                urls = args.urls,
                initialIndex = args.initialIndex,
                shareHelper = instance(),
                galleryHelper = instance(),
                imagePreloadManager = instance(),
            )
        }
    }
