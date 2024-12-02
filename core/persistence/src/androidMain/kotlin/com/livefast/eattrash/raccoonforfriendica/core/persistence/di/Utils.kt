package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.persistence.builder")
internal class NativePersistenceModule

actual val nativePersistenceModule: org.koin.core.module.Module = NativePersistenceModule().module
