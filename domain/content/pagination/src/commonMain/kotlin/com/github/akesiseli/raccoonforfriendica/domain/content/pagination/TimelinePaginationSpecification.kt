package com.github.akesiseli.raccoonforfriendica.domain.content.pagination

import com.github.akesiseli.raccoonforfriendica.domain.content.data.TimelineType

sealed interface TimelinePaginationSpecification {
    data class Feed(
        val timelineType: TimelineType,
    ) : TimelinePaginationSpecification

    data class Hashtag(
        val hashtag: String,
    ) : TimelinePaginationSpecification
}
