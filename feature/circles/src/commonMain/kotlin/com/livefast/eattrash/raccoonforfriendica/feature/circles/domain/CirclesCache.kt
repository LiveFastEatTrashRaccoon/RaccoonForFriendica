package com.livefast.eattrash.raccoonforfriendica.feature.circles.domain

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel

interface CirclesCache {
    suspend fun get(): List<CircleModel>?

    suspend fun put(value: List<CircleModel>)

    suspend fun get(id: String): CircleModel?

    suspend fun put(
        id: String,
        circle: CircleModel,
    )
}
