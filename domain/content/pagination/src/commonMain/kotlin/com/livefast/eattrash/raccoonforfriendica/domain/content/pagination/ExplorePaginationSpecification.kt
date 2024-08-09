package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface ExplorePaginationSpecification {
    data object Posts : ExplorePaginationSpecification

    data object Hashtags : ExplorePaginationSpecification

    data object Links : ExplorePaginationSpecification

    data object Suggestions : ExplorePaginationSpecification
}
