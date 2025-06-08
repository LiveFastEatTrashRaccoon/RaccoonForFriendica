package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerType

interface MarkerRepository {
    suspend fun get(type: MarkerType, refresh: Boolean = false): MarkerModel?

    suspend fun update(type: MarkerType, id: String): MarkerModel?
}
