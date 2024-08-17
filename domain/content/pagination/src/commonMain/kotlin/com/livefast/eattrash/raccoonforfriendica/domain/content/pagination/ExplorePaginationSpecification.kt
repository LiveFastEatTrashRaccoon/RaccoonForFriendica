package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface ExplorePaginationSpecification {
    data class Posts(
        val includeNsfw: Boolean = true,
    ) : ExplorePaginationSpecification

    data object Hashtags : ExplorePaginationSpecification

    data object Links : ExplorePaginationSpecification

    data object Suggestions : ExplorePaginationSpecification
}
