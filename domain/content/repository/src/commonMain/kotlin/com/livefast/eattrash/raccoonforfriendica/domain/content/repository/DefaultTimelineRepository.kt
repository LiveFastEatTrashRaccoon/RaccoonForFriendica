package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.extractNextIdFromResponseLinkHeader
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModelWithReply
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class DefaultTimelineRepository(
    @Named("default") private val provider: ServiceProvider,
) : TimelineRepository {
    private val mutex = Mutex()
    private val cachedValues: MutableList<TimelineEntryModel> = mutableListOf()

    override suspend fun getPublic(
        pageCursor: String?,
        refresh: Boolean,
    ): List<TimelineEntryModel>? =
        withContext(Dispatchers.IO) {
            if (refresh) {
                mutex.withLock {
                    cachedValues.clear()
                }
            }
            if (pageCursor == null && cachedValues.isNotEmpty()) {
                return@withContext cachedValues
            }
            runCatching {
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

    override suspend fun getHome(
        pageCursor: String?,
        refresh: Boolean,
    ): List<TimelineEntryModel>? =
        withContext(Dispatchers.IO) {
            if (refresh) {
                mutex.withLock {
                    cachedValues.clear()
                }
            }
            if (pageCursor == null && cachedValues.isNotEmpty()) {
                return@withContext cachedValues
            }
            runCatching {
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
    ): List<TimelineEntryModel>? =
        withContext(Dispatchers.IO) {
            if (refresh) {
                mutex.withLock {
                    cachedValues.clear()
                }
            }
            if (pageCursor == null && cachedValues.isNotEmpty()) {
                return@withContext cachedValues
            }
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
                        if (pageCursor == null) {
                            mutex.withLock {
                                cachedValues.addAll(it)
                            }
                        }
                    }
            }.getOrNull()
        }

    override suspend fun getHashtag(
        hashtag: String,
        pageCursor: String?,
    ): ListWithPageCursor<TimelineEntryModel>? =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.timeline.getHashtag(
                        hashtag = hashtag,
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                    )
                val list = response.body()?.map { it.toModelWithReply() }.orEmpty()
                val nextCursor: String? = response.extractNextIdFromResponseLinkHeader()
                ListWithPageCursor(list = list, cursor = nextCursor)
            }.getOrNull()
        }

    override suspend fun getCircle(
        id: String,
        pageCursor: String?,
    ): List<TimelineEntryModel>? =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.timeline.getList(
                        id = id,
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                    )
                response.map { it.toModelWithReply() }
            }.getOrNull()
        }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
