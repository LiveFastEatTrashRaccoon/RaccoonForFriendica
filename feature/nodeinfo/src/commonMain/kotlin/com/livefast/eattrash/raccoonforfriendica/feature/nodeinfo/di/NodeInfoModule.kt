package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo")
internal class NodeInfoModule

val featureNodeInfoModule = NodeInfoModule().module
