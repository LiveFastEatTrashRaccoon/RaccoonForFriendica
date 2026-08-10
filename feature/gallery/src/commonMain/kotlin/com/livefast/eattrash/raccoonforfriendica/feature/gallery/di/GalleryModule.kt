package com.livefast.eattrash.raccoonforfriendica.feature.gallery.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class AlbumDetailViewModelArgs(val albumName: String)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.gallery")
class GalleryModule
