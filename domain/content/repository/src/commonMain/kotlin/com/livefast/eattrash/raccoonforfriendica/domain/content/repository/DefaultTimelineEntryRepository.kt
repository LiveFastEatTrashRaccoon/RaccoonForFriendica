package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusAddons
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateStatusForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.ReblogPostForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.SubmitPollVoteForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModelWithReply
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
            }.getOrElse { emptyList() }
        }

    override suspend fun getById(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.get(id = id).toModelWithReply()
            }.getOrNull()
        }

    override suspend fun getSource(id: String): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.getSource(id = id).toModel()
            }.getOrNull()
        }

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
            }.getOrNull()
        }

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
            }.getOrElse { emptyList() }
        }

    override suspend fun getBookmarks(pageCursor: String?): List<TimelineEntryModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.users
                    .getBookmarks(
                        maxId = pageCursor,
                        limit = DefaultTimelineRepository.DEFAULT_PAGE_SIZE,
                    ).map { it.toModelWithReply() }
            }.getOrElse { emptyList() }
        }

    override suspend fun getUsersWhoFavorited(
        id: String,
        pageCursor: String?,
    ): List<UserModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses
                    .getFavoritedBy(
                        id = id,
                        maxId = pageCursor,
                        limit = DefaultTimelineRepository.DEFAULT_PAGE_SIZE,
                    ).map { it.toModel() }
            }.getOrElse { emptyList() }
        }

    override suspend fun getUsersWhoReblogged(
        id: String,
        pageCursor: String?,
    ): List<UserModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses
                    .getRebloggedBy(
                        id = id,
                        maxId = pageCursor,
                        limit = DefaultTimelineRepository.DEFAULT_PAGE_SIZE,
                    ).map { it.toModel() }
            }.getOrElse { emptyList() }
        }

    override suspend fun create(
        localId: String,
        text: String,
        title: String?,
        spoilerText: String?,
        inReplyTo: String?,
        sensitive: Boolean,
        mediaIds: List<String>?,
        visibility: Visibility,
        lang: String?,
    ): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                val data =
                    CreateStatusForm(
                        status = text,
                        addons = StatusAddons(title = title.orEmpty()),
                        mediaIds = mediaIds,
                        visibility = visibility.toDto(),
                        sensitive = sensitive,
                        inReplyTo = inReplyTo,
                        lang = lang,
                        spoilerText = spoilerText,
                    )
                provider.statuses
                    .create(
                        key = localId,
                        data = data,
                    ).toModel()
            }.getOrNull()
        }

    override suspend fun update(
        id: String,
        text: String,
        title: String?,
        spoilerText: String?,
        inReplyTo: String?,
        sensitive: Boolean,
        mediaIds: List<String>?,
        visibility: Visibility,
        lang: String?,
    ): TimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                val data =
                    CreateStatusForm(
                        status = text,
                        addons = StatusAddons(title = title.orEmpty()),
                        mediaIds = mediaIds,
                        visibility = visibility.toDto(),
                        sensitive = sensitive,
                        inReplyTo = inReplyTo,
                        lang = lang,
                        spoilerText = spoilerText,
                    )
                provider.statuses
                    .update(
                        id = id,
                        data = data,
                    ).toModel()
            }.getOrNull()
        }

    override suspend fun delete(id: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.statuses.delete(id)
                true
            }.getOrElse { false }
        }

    override suspend fun submitPoll(
        pollId: String,
        choices: List<Int>,
    ): PollModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                val data =
                    SubmitPollVoteForm(
                        choices = choices,
                    )
                provider.polls
                    .vote(
                        id = pollId,
                        data = data,
                    ).toModel()
            }.getOrNull()
        }
}
