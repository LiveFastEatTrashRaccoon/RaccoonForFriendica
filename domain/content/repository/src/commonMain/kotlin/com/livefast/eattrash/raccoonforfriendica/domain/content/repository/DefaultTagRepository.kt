package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultTagRepository(
    private val provider: ServiceProvider,
) : TagRepository {
    override suspend fun getFollowed(): List<TagModel> =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.tags.getFollowedTags().map { it.toModel() }
            }
        }.getOrElse { emptyList() }

    override suspend fun getBy(name: String): TagModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.tags.get(name)?.toModel()
            }
        }.getOrNull()

    override suspend fun follow(name: String): TagModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.tags.follow(name)?.toModel()
            }
        }.getOrNull()

    override suspend fun unfollow(name: String): TagModel? =
        runCatching {
            withContext(Dispatchers.IO) {
                provider.tags.unfollow(name)?.toModel()
            }
        }.getOrNull()
}
