package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.parameters

internal class DefaultScheduledEntryRepository(private val provider: ServiceProvider) : ScheduledEntryRepository {
    override suspend fun getAll(pageCursor: String?): List<TimelineEntryModel>? = runCatching {
        provider.statuses
            .getScheduled(
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    }.getOrNull()

    override suspend fun getById(id: String): TimelineEntryModel? = runCatching {
        provider.statuses.getScheduledById(id).toModel()
    }.getOrNull()

    override suspend fun update(id: String, date: String): TimelineEntryModel? = runCatching {
        val data =
            FormDataContent(
                parameters {
                    append("scheduled_at", date)
                },
            )
        provider.statuses
            .updateScheduled(
                id = id,
                data = data,
            ).toModel()
    }.getOrNull()

    override suspend fun delete(id: String): Boolean = provider.statuses.deleteScheduled(id)

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
