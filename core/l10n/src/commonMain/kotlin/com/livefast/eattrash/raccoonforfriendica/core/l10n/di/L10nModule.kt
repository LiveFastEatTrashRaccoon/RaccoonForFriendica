package com.livefast.eattrash.raccoonforfriendica.core.l10n.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.l10n")
internal class L10nModule

val coreL10nModule = L10nModule().module
