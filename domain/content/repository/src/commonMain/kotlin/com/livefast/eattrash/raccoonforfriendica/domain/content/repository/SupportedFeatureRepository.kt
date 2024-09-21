package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeFeatures
import kotlinx.coroutines.flow.StateFlow

interface SupportedFeatureRepository {
    val features: StateFlow<NodeFeatures>

    suspend fun refresh()
}
