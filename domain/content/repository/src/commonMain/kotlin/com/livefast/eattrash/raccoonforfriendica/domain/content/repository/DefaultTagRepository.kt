package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel

internal class DefaultTagRepository(private val provider: ServiceProvider) : TagRepository {
    override suspend fun getFollowed(pageCursor: String?): ListWithPageCursor<TagModel>? = runCatching {
        val (list, cursor) =
            provider.tags.getFollowedTags(
                maxId = pageCursor,
            )
        ListWithPageCursor(list = list.map { it.toModel() }, cursor = cursor)
    }.getOrNull()

    override suspend fun getBy(name: String): TagModel? = runCatching {
        provider.tags.get(name).toModel()
    }.getOrNull()

    override suspend fun follow(name: String): TagModel? = runCatching {
        provider.tags.follow(name).toModel()
    }.getOrNull()

    override suspend fun unfollow(name: String): TagModel? = runCatching {
        provider.tags.unfollow(name).toModel()
    }.getOrNull()
}
