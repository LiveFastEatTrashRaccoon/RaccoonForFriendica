package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface FavoritesPaginationSpecification {
    data object Favorites : FavoritesPaginationSpecification

    data object Bookmarks : FavoritesPaginationSpecification
}
