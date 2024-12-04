package com.livefast.eattrash.raccoonforfriendica.feaure.search.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feaure.search")
internal class SearchModule

val featureSearchModule = SearchModule().module
