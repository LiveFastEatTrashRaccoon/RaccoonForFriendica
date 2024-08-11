package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultTagRepository(
    private val provider: ServiceProvider,
) : TagRepository {
    override suspend fun getFollowed(pageCursor: String?): Pair<List<TagModel>, String?> =
        runCatching {
            withContext(Dispatchers.IO) {
                val response =
                    provider.tags.getFollowedTags(
                        maxId = pageCursor,
                    )
                val list: List<TagModel> = response.body()?.map { it.toModel() }.orEmpty()
                val nextCursor: String? =
                    response.headers["link"]
                        .orEmpty()
                        .let {
                            val match = Regex("max_id=(?<maxId>\\d+)>").find(it)
                            match?.groups?.get("maxId")?.value
                        }
                list to nextCursor
            }
        }.getOrElse { emptyList<TagModel>() to null }

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
