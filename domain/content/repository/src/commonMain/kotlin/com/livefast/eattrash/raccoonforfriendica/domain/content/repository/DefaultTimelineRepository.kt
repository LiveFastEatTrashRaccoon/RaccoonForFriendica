package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModelWithReply
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultTimelineRepository(
    private val provider: ServiceProvider,
    private val otherProvider: ServiceProvider,
) : TimelineRepository {
    private val mutex = Mutex()
    private val cachedValues: MutableList<TimelineEntryModel> = mutableListOf()

    override suspend fun getPublic(pageCursor: String?, refresh: Boolean): List<TimelineEntryModel>? {
        if (refresh) {
            mutex.withLock {
                cachedValues.clear()
            }
        }
        if (pageCursor == null && cachedValues.isNotEmpty()) {
            return cachedValues
        }
        return runCatching {
            val response =
                provider.timeline.getPublic(
                    maxId = pageCursor,
                    limit = DEFAULT_PAGE_SIZE,
                )
            response
                .map { it.toModelWithReply() }
                .also {
                    if (pageCursor == null) {
                        mutex.withLock {
                            cachedValues.addAll(it)
                        }
                    }
                }
        }.getOrNull()
    }

    override suspend fun getHome(pageCursor: String?, refresh: Boolean): List<TimelineEntryModel>? {
        if (refresh) {
            mutex.withLock {
                cachedValues.clear()
            }
        }
        if (pageCursor == null && cachedValues.isNotEmpty()) {
            return cachedValues
        }
        return runCatching {
            val response =
                provider.timeline.getHome(
                    maxId = pageCursor,
                    limit = DEFAULT_PAGE_SIZE,
                )
            response
                .map { it.toModelWithReply() }
                .also {
                    if (pageCursor == null) {
                        mutex.withLock {
                            cachedValues.addAll(it)
                        }
                    }
                }
        }.getOrNull()
    }

    override suspend fun getLocal(
        pageCursor: String?,
        refresh: Boolean,
        otherInstance: String?,
    ): List<TimelineEntryModel>? = withProvider(otherInstance) { provider ->
        if (refresh && otherInstance.isNullOrEmpty()) {
            mutex.withLock {
                cachedValues.clear()
            }
        }
        if (pageCursor == null && cachedValues.isNotEmpty() && otherInstance.isNullOrEmpty()) {
            cachedValues
        } else {
            runCatching {
                val response =
                    provider.timeline.getPublic(
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                        local = true,
                    )
                response
                    .map { it.toModelWithReply() }
                    .also {
                        if (pageCursor == null && otherInstance.isNullOrEmpty()) {
                            mutex.withLock {
                                cachedValues.addAll(it)
                            }
                        }
                    }
            }.getOrNull()
        }
    }

    override suspend fun getHashtag(
        hashtag: String,
        pageCursor: String?,
        otherInstance: String?,
    ): ListWithPageCursor<TimelineEntryModel>? = runCatching {
        val (list, cursor) = withProvider(otherInstance) { provider ->
            provider.timeline.getHashtag(
                hashtag = hashtag,
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            )
        }
        ListWithPageCursor(list = list.map { it.toModelWithReply() }, cursor = cursor)
    }.getOrNull()

    override suspend fun getCircle(id: String, pageCursor: String?): List<TimelineEntryModel>? = runCatching {
        val response =
            provider.timeline.getList(
                id = id,
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            )
        response.map { it.toModelWithReply() }
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
        const val DEFAULT_PAGE_SIZE = 20
    }
}
