package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface SearchPaginationSpecification {
    data class Entries(val query: String, val includeNsfw: Boolean = true) : SearchPaginationSpecification

    data class Users(val query: String) : SearchPaginationSpecification

    data class Hashtags(val query: String) : SearchPaginationSpecification

    data class Groups(val query: String) : SearchPaginationSpecification
}
