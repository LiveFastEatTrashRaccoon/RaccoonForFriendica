package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType

sealed interface TimelinePaginationSpecification {
    data class Feed(
        val timelineType: TimelineType,
    ) : TimelinePaginationSpecification

    data class Hashtag(
        val hashtag: String,
    ) : TimelinePaginationSpecification

    data class Account(
        val accountId: String,
        val onlyMedia: Boolean = false,
        val excludeReplies: Boolean = true,
        val excludeReblogs: Boolean = true,
        val pinned: Boolean = false,
    ) : TimelinePaginationSpecification
}
