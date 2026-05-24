package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.utils.io.CancellationException

internal class DefaultTagRepository(private val provider: ServiceProvider) : TagRepository {
    override suspend fun getFollowed(pageCursor: String?): ListWithPageCursor<TagModel>? = try {
        val (list, cursor) =
            provider.tag.getFollowedTags(
                maxId = pageCursor,
            )
        ListWithPageCursor(list = list.map { it.toModel() }, cursor = cursor)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getBy(name: String): TagModel? = try {
        provider.tag.get(name).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun follow(name: String): TagModel? = try {
        provider.tag.follow(name).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun unfollow(name: String): TagModel? = try {
        provider.tag.unfollow(name).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }
}
