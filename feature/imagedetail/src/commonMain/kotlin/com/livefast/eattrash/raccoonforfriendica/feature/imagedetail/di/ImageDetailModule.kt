package com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.di

import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

internal data class ImageDetailMviModelParams(
    val urls: List<String>,
    val initialIndex: Int = 0,
)

val imageDetailModule =
    DI.Module("ImageDetailModule") {
        bind<ImageDetailMviModel> {
            factory { params: ImageDetailMviModelParams ->
                ImageDetailViewModel(
                    urls = params.urls,
                    initialIndex = params.initialIndex,
                    shareHelper = instance(),
                    galleryHelper = instance(),
                    imagePreloadManager = instance(),
                )
            }
        }
    }
