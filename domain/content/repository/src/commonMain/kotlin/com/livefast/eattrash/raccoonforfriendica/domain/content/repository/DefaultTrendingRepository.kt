package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModelWithReply
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultTrendingRepository(
    private val provider: ServiceProvider,
) : TrendingRepository {
    override suspend fun getEntries(offset: Int): List<TimelineEntryModel>? =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.trends
                        .getStatuses(
                            offset = offset,
                            limit = DEFAULT_PAGE_SIZE,
                        )
                response.map { it.toModelWithReply() }
            }.getOrNull()
        }

    override suspend fun getHashtags(offset: Int): List<TagModel>? =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.trends
                        .getHashtags(
                            offset = offset,
                            limit = DEFAULT_PAGE_SIZE,
                        )
                response.map { it.toModel() }
            }.getOrNull()
        }

    override suspend fun getLinks(offset: Int): List<LinkModel>? =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.trends
                        .getLinks(
                            offset = offset,
                            limit = DEFAULT_PAGE_SIZE,
                        )
                response.map { it.toModel() }
            }.getOrNull()
        }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
