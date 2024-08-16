package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

internal class DefaultTrendingRepository(
    private val provider: ServiceProvider,
) : TrendingRepository {
    override suspend fun getEntries(offset: Int): List<TimelineEntryModel> =
        runCatching {
            val response =
                provider.trends
                    .getStatuses(
                        offset = offset,
                        limit = DEFAULT_PAGE_SIZE,
                    )
            response.map { it.toModelWithReply() }
        }.getOrElse { emptyList() }

    override suspend fun getHashtags(offset: Int): List<TagModel> =
        runCatching {
            val response =
                provider.trends
                    .getHashtags(
                        offset = offset,
                        limit = DEFAULT_PAGE_SIZE,
                    )
            response.map { it.toModel() }
        }.getOrElse { emptyList() }

    override suspend fun getLinks(offset: Int): List<LinkModel> =
        runCatching {
            val response =
                provider.trends
                    .getLinks(
                        offset = offset,
                        limit = DEFAULT_PAGE_SIZE,
                    )
            response.map { it.toModel() }
        }.getOrElse { emptyList() }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
