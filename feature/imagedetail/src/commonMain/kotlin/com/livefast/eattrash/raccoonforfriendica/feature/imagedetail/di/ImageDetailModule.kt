package com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.di

import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailViewModel
import org.koin.dsl.module

val featureImageDetailModule =
    module {
        factory<ImageDetailMviModel> { params ->
            ImageDetailViewModel(
                url = params[0],
                shareHelper = get(),
                galleryHelper = get(),
                imagePreloadManager = get(),
            )
        }
    }
