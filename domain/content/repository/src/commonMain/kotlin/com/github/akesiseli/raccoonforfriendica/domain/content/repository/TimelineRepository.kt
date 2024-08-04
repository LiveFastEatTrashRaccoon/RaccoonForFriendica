package com.github.akesiseli.raccoonforfriendica.domain.content.repository

import com.github.akesiseli.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface TimelineRepository {
    suspend fun getPublic(pageCursor: String? = null): List<TimelineEntryModel>

    suspend fun getHome(pageCursor: String? = null): List<TimelineEntryModel>

    suspend fun getHashtag(
        hashtag: String,
        pageCursor: String? = null,
    ): List<TimelineEntryModel>
}
