package com.github.akesiseli.raccoonforfriendica.domain.content.repository

import com.github.akesiseli.raccoonforfriendica.core.api.provider.ServiceProvider
import com.github.akesiseli.raccoonforfriendica.domain.content.data.TimelineEntryModel
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
                response.map { it.toModel() }
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
                response.map { it.toModel() }
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
                response.map { it.toModel() }
            }.getOrElse { emptyList() }
        }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
