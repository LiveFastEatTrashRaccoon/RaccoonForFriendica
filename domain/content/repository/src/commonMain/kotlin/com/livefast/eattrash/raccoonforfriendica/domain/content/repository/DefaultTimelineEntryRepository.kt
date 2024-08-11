package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.form.ReblogPostForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultTimelineEntryRepository(
    private val provider: ServiceProvider,
) : TimelineEntryRepository {
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
                provider.users
                    .getStatuses(
                        id = userId,
                        excludeReblogs = excludeReblogs,
                        maxId = pageCursor,
                        excludeReplies = excludeReplies,
                        pinned = pinned,
                        onlyMedia = onlyMedia,
                        limit = DefaultTimelineRepository.DEFAULT_PAGE_SIZE,
                    ).map { it.toModelWithReply() }
            }
        }.getOrElse { emptyList() }

    override suspend fun getById(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.get(id = id).toModelWithReply()
            }
        }.getOrNull()

    override suspend fun getContext(id: String): TimelineContextModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.getContext(id = id).toModel()
            }.getOrNull()
        }

    override suspend fun reblog(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                val data = ReblogPostForm()
                provider.statuses
                    .reblog(
                        id = id,
                        data = data,
                    ).toModelWithReply()
            }
        }.getOrNull()

    override suspend fun unreblog(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.unreblog(id).toModel()
            }.getOrNull()
        }

    override suspend fun pin(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.pin(id).toModel()
            }.getOrNull()
        }

    override suspend fun unpin(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.unpin(id).toModel()
            }.getOrNull()
        }

    override suspend fun favorite(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.favorite(id).toModel()
            }.getOrNull()
        }

    override suspend fun unfavorite(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.unfavorite(id).toModel()
            }.getOrNull()
        }

    override suspend fun bookmark(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.bookmark(id).toModel()
            }.getOrNull()
        }

    override suspend fun unbookmark(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.unbookmark(id).toModel()
            }.getOrNull()
        }

    override suspend fun getFavorites(pageCursor: String?): List<TimelineEntryModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.users
                    .getFavorites(
                        maxId = pageCursor,
                        limit = DefaultTimelineRepository.DEFAULT_PAGE_SIZE,
                    ).map { it.toModelWithReply() }
            }
        }.getOrElse { emptyList() }

    override suspend fun getBookmarks(pageCursor: String?): List<TimelineEntryModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.users
                    .getBookmarks(
                        maxId = pageCursor,
                        limit = DefaultTimelineRepository.DEFAULT_PAGE_SIZE,
                    ).map { it.toModelWithReply() }
            }
        }.getOrElse { emptyList() }
}
