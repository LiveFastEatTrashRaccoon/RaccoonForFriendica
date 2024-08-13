package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface UserPaginationSpecification {
    data class Follower(
        val userId: String,
    ) : UserPaginationSpecification

    data class Following(
        val userId: String,
    ) : UserPaginationSpecification

    data class EntryUsersReblog(
        val entryId: String,
    ) : UserPaginationSpecification

    data class EntryUsersFavorite(
        val entryId: String,
    ) : UserPaginationSpecification

    data class Search(
        val query: String = "",
    ) : UserPaginationSpecification
}
