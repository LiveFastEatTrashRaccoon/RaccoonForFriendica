package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.parameters
import io.ktor.utils.io.CancellationException

internal class DefaultScheduledEntryRepository(private val provider: ServiceProvider) : ScheduledEntryRepository {
    override suspend fun getAll(pageCursor: String?): List<TimelineEntryModel>? = try {
        provider.status
            .getScheduled(
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getById(id: String): TimelineEntryModel? = try {
        provider.status.getScheduledById(id).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun update(id: String, date: String): TimelineEntryModel? = try {
        val data =
            FormDataContent(
                parameters {
                    append("scheduled_at", date)
                },
            )
        provider.status
            .updateScheduled(
                id = id,
                data = data,
            ).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun delete(id: String): Boolean = provider.status.deleteScheduled(id)

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
