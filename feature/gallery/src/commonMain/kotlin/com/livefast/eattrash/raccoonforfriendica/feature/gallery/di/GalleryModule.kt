package com.livefast.eattrash.raccoonforfriendica.feature.gallery.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.gallery")
internal class GalleryModule

val featureGalleryModule = GalleryModule().module
