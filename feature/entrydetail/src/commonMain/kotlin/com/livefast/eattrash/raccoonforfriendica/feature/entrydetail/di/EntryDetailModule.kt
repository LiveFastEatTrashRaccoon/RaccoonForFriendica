package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.entrydetail")
internal class EntryDetailModule

val featureEntryDetailModule = EntryDetailModule().module
