package com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.imagedetail")
class ImageDetailModule

val featureImageDetailModule = ImageDetailModule().module
