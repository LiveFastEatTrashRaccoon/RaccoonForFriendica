package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DraftDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.DraftEntity
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollOptionModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toVisibility

internal class DefaultDraftRepository(private val draftDao: DraftDao, private val provider: ServiceProvider) :
    DraftRepository {
    override suspend fun getAll(page: Int): List<TimelineEntryModel>? = runCatching {
        draftDao
            .getAll(
                offset = page * DEFAULT_PAGE_SIZE,
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    }.getOrNull()

    override suspend fun getById(id: String): TimelineEntryModel? = runCatching {
        draftDao.getBy(id)?.toModel()
    }.getOrNull()

    override suspend fun create(item: TimelineEntryModel): TimelineEntryModel? = runCatching {
        val entity = item.toEntity()
        draftDao.insert(entity)
        getById(item.id)
    }.getOrNull()

    override suspend fun update(item: TimelineEntryModel): TimelineEntryModel? = runCatching {
        val entity = item.toEntity()
        draftDao.update(entity)
        getById(item.id)
    }.getOrNull()

    override suspend fun delete(id: String): Boolean = runCatching {
        val entity = DraftEntity(id = id)
        draftDao.delete(entity)
        true
    }.getOrElse { false }

    private suspend fun DraftEntity.toModel() = TimelineEntryModel(
        id = id,
        attachments =
        mediaIds
            ?.split(",")
            ?.filterNot { it.isEmpty() }
            ?.mapNotNull { id ->
                runCatching { provider.media.getBy(id) }.getOrNull()?.toModel()
            }.orEmpty(),
        parentId = inReplyToId,
        lang = lang,
        localOnly = localOnly,
        content = text.orEmpty(),
        sensitive = sensitive == true,
        spoiler = spoiler,
        title = title,
        created = created,
        visibility = visibility?.toVisibility() ?: Visibility.Public,
        poll =
        run {
            val expire = pollExpiresAt
            val options =
                pollOptions
                    ?.split(",")
                    ?.map {
                        PollOptionModel(title = it)
                    }.orEmpty()
            val multiple = pollMultiple == true
            if (options.isEmpty()) {
                null
            } else {
                PollModel(
                    id = "",
                    expiresAt = expire,
                    multiple = multiple,
                    options = options,
                )
            }
        },
    )

    private fun TimelineEntryModel.toEntity() = DraftEntity(
        id = id,
        mediaIds = attachments.joinToString(",") { it.id },
        inReplyToId = parentId,
        lang = lang,
        localOnly = localOnly,
        sensitive = sensitive,
        spoiler = spoiler,
        title = title,
        text = content,
        created = created,
        visibility = visibility.toDto(),
        pollMultiple = poll?.multiple,
        pollExpiresAt = poll?.expiresAt,
        pollOptions = poll?.options?.joinToString("m") { it.title },
    )

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
