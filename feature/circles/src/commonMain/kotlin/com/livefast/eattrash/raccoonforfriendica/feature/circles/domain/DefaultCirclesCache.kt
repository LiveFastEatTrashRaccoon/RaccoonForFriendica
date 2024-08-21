package com.livefast.eattrash.raccoonforfriendica.feature.circles.domain

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel

internal class DefaultCirclesCache : CirclesCache {
    private var cachedCircles = mutableMapOf<String, CircleModel>()

    override suspend fun get(): List<CircleModel>? = cachedCircles.values.sortedBy { e -> e.id }.takeIf { it.isNotEmpty() }

    override suspend fun put(value: List<CircleModel>) {
        cachedCircles.putAll(value.map { it.id to it })
    }

    override suspend fun get(id: String): CircleModel? = cachedCircles[id]

    override suspend fun put(
        id: String,
        circle: CircleModel,
    ) {
        cachedCircles[id] = circle
    }
}
