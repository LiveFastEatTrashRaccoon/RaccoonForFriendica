package com.livefast.eattrash.raccoonforfriendica.feature.composer.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class ComposerViewModelArgs(val inReplyToId: String?, val quotedId: String?)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.composer")
class ComposerModule
