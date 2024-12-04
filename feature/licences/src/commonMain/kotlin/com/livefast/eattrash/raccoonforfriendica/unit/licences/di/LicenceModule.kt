package com.livefast.eattrash.raccoonforfriendica.unit.licences.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.unit.licences")
internal class LicenceModule

val featureLicenceModule = LicenceModule().module
