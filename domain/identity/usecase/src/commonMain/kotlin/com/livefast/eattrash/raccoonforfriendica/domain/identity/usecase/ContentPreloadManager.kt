package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType

interface ContentPreloadManager {
    suspend fun preload(
        userRemoteId: String? = null,
        defaultTimelineType: TimelineType = TimelineType.Local,
    )
}
