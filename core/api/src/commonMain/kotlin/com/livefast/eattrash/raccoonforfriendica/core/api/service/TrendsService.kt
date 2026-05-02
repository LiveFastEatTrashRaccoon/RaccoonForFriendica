package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink

interface TrendsService {
    suspend fun getHashtags(offset: Int, limit: Int = 20): List<Tag>

    suspend fun getStatuses(offset: Int, limit: Int = 20): List<Status>

    suspend fun getLinks(offset: Int, limit: Int = 20): List<TrendsLink>
}
