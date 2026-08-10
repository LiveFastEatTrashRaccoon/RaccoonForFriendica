package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class ConversationViewModelArgs(val otherUserId: String, val parentUri: String)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.directmessages")
class DirectMessagesModule
