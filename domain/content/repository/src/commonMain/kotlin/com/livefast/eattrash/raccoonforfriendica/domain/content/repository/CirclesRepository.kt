package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel

interface CirclesRepository {
    suspend fun getAll(): List<CircleModel>
}
