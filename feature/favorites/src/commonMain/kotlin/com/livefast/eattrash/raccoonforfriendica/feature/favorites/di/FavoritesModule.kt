package com.livefast.eattrash.raccoonforfriendica.feature.favorites.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.favorites")
internal class FavoritesModule

val featureFavoritesModule = FavoritesModule().module
