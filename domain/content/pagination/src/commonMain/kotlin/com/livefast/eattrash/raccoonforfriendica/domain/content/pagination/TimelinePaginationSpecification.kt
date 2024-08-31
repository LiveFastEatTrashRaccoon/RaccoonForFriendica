package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType

sealed interface TimelinePaginationSpecification {
    data class Feed(
        val timelineType: TimelineType,
        val includeNsfw: Boolean = true,
    ) : TimelinePaginationSpecification

    data class Hashtag(
        val hashtag: String,
        val includeNsfw: Boolean = true,
    ) : TimelinePaginationSpecification

    data class User(
        val userId: String,
        val onlyMedia: Boolean = false,
        val excludeReplies: Boolean = true,
        val excludeReblogs: Boolean = false,
        val pinned: Boolean = false,
        val includeNsfw: Boolean = true,
    ) : TimelinePaginationSpecification

    data class Forum(
        val userId: String,
        val includeNsfw: Boolean = true,
    ) : TimelinePaginationSpecification
}
