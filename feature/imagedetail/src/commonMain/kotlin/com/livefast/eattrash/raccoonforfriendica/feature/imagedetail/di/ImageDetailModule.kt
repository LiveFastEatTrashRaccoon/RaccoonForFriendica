package com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.di

import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal data class ImageDetailViewModelArgs(val urls: List<String>, val initialIndex: Int = 0)

val imageDetailModule = module {
    viewModel { params ->
        val args: ImageDetailViewModelArgs = params.get()
        ImageDetailViewModel(
            urls = args.urls,
            initialIndex = args.initialIndex,
            shareHelper = get(),
            galleryHelper = get(),
            imagePreloadManager = get(),
        )
    }
}
