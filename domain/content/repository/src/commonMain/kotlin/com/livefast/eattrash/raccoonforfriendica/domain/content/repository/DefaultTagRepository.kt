package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.extractNextIdFromResponseLinkHeader
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultTagRepository(
    private val provider: ServiceProvider,
) : TagRepository {
    override suspend fun getFollowed(pageCursor: String?): ListWithPageCursor<TagModel>? =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.tags.getFollowedTags(
                        maxId = pageCursor,
                    )
                val list: List<TagModel> = response.body()?.map { it.toModel() }.orEmpty()
                val nextCursor: String? = response.extractNextIdFromResponseLinkHeader()
                ListWithPageCursor(list = list, cursor = nextCursor)
            }.getOrNull()
        }

    override suspend fun getBy(name: String): TagModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.tags.get(name)?.toModel()
            }.getOrNull()
        }

    override suspend fun follow(name: String): TagModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.tags.follow(name)?.toModel()
            }.getOrNull()
        }

    override suspend fun unfollow(name: String): TagModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.tags.unfollow(name)?.toModel()
            }.getOrNull()
        }
}
