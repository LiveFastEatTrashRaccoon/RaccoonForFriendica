package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import kotlinx.coroutines.flow.StateFlow

interface ImageAutoloadObserver {
    val enabled: StateFlow<Boolean>
}
