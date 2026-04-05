package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface TrendingRepository {
    suspend fun getEntries(offset: Int, otherInstance: String? = null): List<TimelineEntryModel>?

    suspend fun getHashtags(offset: Int, refresh: Boolean = false, otherInstance: String? = null): List<TagModel>?

    suspend fun getLinks(offset: Int, otherInstance: String? = null): List<LinkModel>?
}
