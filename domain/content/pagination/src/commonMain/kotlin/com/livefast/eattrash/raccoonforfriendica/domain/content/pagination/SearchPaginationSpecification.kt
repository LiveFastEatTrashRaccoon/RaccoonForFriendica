package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface SearchPaginationSpecification {
    data class Entries(
        val query: String,
    ) : SearchPaginationSpecification

    data class Users(
        val query: String,
    ) : SearchPaginationSpecification

    data class Hashtags(
        val query: String,
    ) : SearchPaginationSpecification
}
