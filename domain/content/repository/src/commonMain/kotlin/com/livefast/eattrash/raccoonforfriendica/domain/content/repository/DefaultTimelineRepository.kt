package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
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

    override suspend fun getByAccount(
        accountId: String,
        pageCursor: String?,
        excludeReplies: Boolean,
        excludeReblogs: Boolean,
        pinned: Boolean,
        onlyMedia: Boolean,
    ): List<TimelineEntryModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.account.getStatuses(
                        id = accountId,
                        excludeReblogs = excludeReblogs,
                        maxId = pageCursor,
                        excludeReplies = excludeReplies,
                        pinned = pinned,
                        onlyMedia = onlyMedia,
                    )
                response.map { it.toModelWithReply() }
            }
        }.getOrElse { emptyList() }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
