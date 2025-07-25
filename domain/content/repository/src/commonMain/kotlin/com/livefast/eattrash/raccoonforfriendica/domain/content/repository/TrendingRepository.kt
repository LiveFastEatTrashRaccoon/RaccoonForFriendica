package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface TrendingRepository {
    suspend fun getEntries(offset: Int): List<TimelineEntryModel>?

    suspend fun getHashtags(offset: Int, refresh: Boolean = false): List<TagModel>?

    suspend fun getLinks(offset: Int): List<LinkModel>?
}
