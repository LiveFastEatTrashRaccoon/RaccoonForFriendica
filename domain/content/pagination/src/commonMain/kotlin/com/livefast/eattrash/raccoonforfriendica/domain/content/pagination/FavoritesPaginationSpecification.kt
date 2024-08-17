package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface FavoritesPaginationSpecification {
    data class Favorites(
        val includeNsfw: Boolean = true,
    ) : FavoritesPaginationSpecification

    data class Bookmarks(
        val includeNsfw: Boolean = true,
    ) : FavoritesPaginationSpecification
}
