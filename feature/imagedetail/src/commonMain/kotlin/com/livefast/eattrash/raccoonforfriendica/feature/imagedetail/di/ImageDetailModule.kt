package com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class ImageDetailViewModelArgs(val urls: List<String>, val initialIndex: Int = 0)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.imagedetail")
class ImageDetailModule
