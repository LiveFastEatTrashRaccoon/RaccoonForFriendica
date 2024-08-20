package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultCirclesRepository(
    private val provider: ServiceProvider,
) : CirclesRepository {
    override suspend fun getAll(): List<CircleModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.circles.getAll().map { it.toModel() }
            }.getOrElse { emptyList() }
        }
}
