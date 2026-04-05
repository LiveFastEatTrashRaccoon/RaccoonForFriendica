package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModelWithReply
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

internal class DefaultTrendingRepository(
    private val provider: ServiceProvider,
    private val otherProvider: ServiceProvider,
    private val json: Json,
) : TrendingRepository {
    private val mutex = Mutex()
    private val cachedTags: MutableList<TagModel> = mutableListOf()

    override suspend fun getEntries(offset: Int, otherInstance: String?): List<TimelineEntryModel>? = runCatching {
        withProvider(otherInstance) { provider ->
            val response =
                provider.trend
                    .getStatuses(
                        offset = offset,
                        limit = DEFAULT_PAGE_SIZE,
                    )
            response.map { it.toModelWithReply() }
        }
    }.getOrNull()

    override suspend fun getHashtags(offset: Int, refresh: Boolean, otherInstance: String?): List<TagModel>? =
        withProvider(otherInstance) { provider ->
            if (refresh && otherInstance.isNullOrEmpty()) {
                mutex.withLock {
                    cachedTags.clear()
                }
            }
            if (offset == 0 && cachedTags.isNotEmpty() && otherInstance.isNullOrEmpty()) {
                cachedTags
            } else {
                runCatching {
                    val response =
                        provider.trend
                            .getHashtags(
                                offset = offset,
                                limit = DEFAULT_PAGE_SIZE,
                            )
                    response
                        .map { it.toModel() }
                        .also {
                            if (offset == 0 && otherInstance.isNullOrEmpty()) {
                                mutex.withLock {
                                    cachedTags.addAll(it)
                                }
                            }
                        }
                }.getOrNull()
            }
        }

    override suspend fun getLinks(offset: Int, otherInstance: String?): List<LinkModel>? = runCatching {
        withProvider(otherInstance) { provider ->
            val response =
                provider.trend
                    .getLinks(
                        offset = offset,
                        limit = DEFAULT_PAGE_SIZE,
                    )
            // workaround for a server bug which inserts empty arrays "[]," among valid results
            // (at least on some Friendica versions)
            val body = response.bodyAsText().replace("[],", "")
            json.decodeFromString<List<TrendsLink>>(body).map { it.toModel() }
        }
    }.getOrNull()

    private suspend fun <T> withProvider(otherInstance: String?, block: suspend (ServiceProvider) -> T): T {
        if (otherInstance.isNullOrEmpty()) {
            return block(provider)
        } else {
            otherProvider.changeNode(otherInstance)
            return block(otherProvider)
        }
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
