package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

internal data class DefaultTimelinePaginationManagerState(
    val specification: TimelinePaginationSpecification? = null,
    val pageCursor: String? = null,
    val history: List<TimelineEntryModel> = emptyList(),
    val userRateLimits: Map<String, Double> = emptyMap(),
) : TimelinePaginationManagerState
