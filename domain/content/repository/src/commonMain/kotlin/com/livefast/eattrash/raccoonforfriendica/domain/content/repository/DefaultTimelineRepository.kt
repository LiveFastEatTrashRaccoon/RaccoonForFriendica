package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultTimelineRepository(
    private val provider: ServiceProvider,
) : TimelineRepository {
    override suspend fun getPublic(pageCursor: String?): List<TimelineEntryModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.timeline.getPublic(
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                    )
                response.map { it.toModelWithReply() }
            }.getOrElse { emptyList() }
        }

    override suspend fun getHome(pageCursor: String?): List<TimelineEntryModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.timeline.getHome(
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                    )
                response.map { it.toModelWithReply() }
            }.getOrElse { emptyList() }
        }

    override suspend fun getLocal(pageCursor: String?): List<TimelineEntryModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.timeline.getPublic(
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                        local = true,
                    )
                response.map { it.toModelWithReply() }
            }.getOrElse { emptyList() }
        }

    override suspend fun getHashtag(
        hashtag: String,
        pageCursor: String?,
    ): List<TimelineEntryModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.timeline.getHashtag(
                        hashtag = hashtag,
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                    )
                response.map { it.toModelWithReply() }
            }.getOrElse { emptyList() }
        }

    override suspend fun getByUser(
        userId: String,
        pageCursor: String?,
        excludeReplies: Boolean,
        excludeReblogs: Boolean,
        pinned: Boolean,
        onlyMedia: Boolean,
    ): List<TimelineEntryModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.users.getStatuses(
                        id = userId,
                        excludeReblogs = excludeReblogs,
                        maxId = pageCursor,
                        excludeReplies = excludeReplies,
                        pinned = pinned,
                        onlyMedia = onlyMedia,
                        limit = DEFAULT_PAGE_SIZE,
                    )
                response.map { it.toModelWithReply() }
            }
        }.getOrElse { emptyList() }

    override suspend fun getById(entryId: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.get(id = entryId).toModelWithReply()
            }
        }.getOrNull()

    override suspend fun getContext(entryId: String): TimelineContextModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = provider.statuses.getContext(id = entryId)
                response.toModel()
            }.getOrNull()
        }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
