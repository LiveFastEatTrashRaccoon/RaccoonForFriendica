package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModelWithReply
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class DefaultTrendingRepository(
    @Named("default") private val provider: ServiceProvider,
) : TrendingRepository {
    private val mutex = Mutex()
    private val cachedTags: MutableList<TagModel> = mutableListOf()

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

    override suspend fun getHashtags(
        offset: Int,
        refresh: Boolean,
    ): List<TagModel>? =
        withContext(Dispatchers.IO) {
            if (refresh) {
                mutex.withLock {
                    cachedTags.clear()
                }
            }
            if (offset == 0 && cachedTags.isNotEmpty()) {
                return@withContext cachedTags
            }
            runCatching {
                val response =
                    provider.trends
                        .getHashtags(
                            offset = offset,
                            limit = DEFAULT_PAGE_SIZE,
                        )
                response
                    .map { it.toModel() }
                    .also {
                        if (offset == 0) {
                            mutex.withLock {
                                cachedTags.addAll(it)
                            }
                        }
                    }
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
                // workaround for a server bug which inserts empty arrays "[]," among valid results
                // (at least on some Friendica versions)
                val body = response.raw().bodyAsText().replace("[],", "")
                TrendsLink.fromJson(body).map { it.toModel() }
            }.getOrNull()
        }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
